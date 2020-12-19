import React from "react";
import {
  Box,
  Button,
  FormControl,
  FormHelperText,
  FormLabel,
  Input,
  NumberInput,
  NumberInputField,
  NumberInputStepper,
  NumberIncrementStepper,
  NumberDecrementStepper,
  Spinner,
} from "@chakra-ui/core";
import { withSnackbar } from "notistack";
import AddQuestions from "../AddQuestions";
import { formatFieldToTitle } from "../../helpers";

const firstStage = [
  {
    name: "name",
    type: "string",
    info: "",
    placeholder: "CSC343 A1",
    required: true,
  },
  {
    name: "submission_file_name",
    type: "string",
    placeholder: "queries.sql",
    info: "",
    required: true,
  },
];

function TaskDetailsForm({
  connectorInfo,
  isLoadingConnectorInfo,
  questions,
  setFieldValue,
  setStage,
  stageTwoSchema,
  values,
}) {
  if (!connectorInfo || isLoadingConnectorInfo) {
    return (
      <Spinner
        color="blue.500"
        emptyColor="gray.200"
        size="xl"
        speed="0.65s"
        thickness="4px"
      />
    );
  }

  const info = connectorInfo.info;
  const extraFields = connectorInfo.extra_fields;

  function validate() {
    try {
      stageTwoSchema.validateSync({
        name: values.name,
        max_marks_per_question: values.max_marks_per_question,
        question_names: values.question_names,
        submission_file_name: values.submission_file_name,
        extra_fields: values.extra_fields,
      });
      return false;
    } catch (e) {
      return true;
    }
  }

  function handleChange(e, name, type, isExtraField) {
    let value;
    if (type === "number") value = e;
    else value = e.target.value;

    if (isExtraField) {
      setFieldValue("extra_fields", {
        ...values.extra_fields,
        [name]: value,
      });
    } else {
      setFieldValue(name, value);
    }
  }

  function renderField(name, field, key, isExtraField) {
    const type = field.type;
    const placeholder = field.placeholder;
    const info = field.info;
    let input;
    if (type === "number") {
      input = (
        <NumberInput
          min={0}
          defaultValue={0}
          value={values[name]}
          onChange={(value) =>
            handleChange(value, name, "number", isExtraField)
          }
        >
          <NumberInputField />
          <NumberInputStepper>
            <NumberIncrementStepper />
            <NumberDecrementStepper />
          </NumberInputStepper>
        </NumberInput>
      );
    } else {
      input = (
        <Input
          aria-label={name}
          name={name}
          placeholder={placeholder}
          value={values[name]}
          onChange={(e) => handleChange(e, name, "string", isExtraField)}
        />
      );
    }

    return (
      <FormControl key={key} mt={4} htmlFor={name}>
        <Box d="flex" justifyContent="left">
          <FormLabel htmlFor={name}>{formatFieldToTitle(name)}</FormLabel>
        </Box>
        {input}
        <Box d="flex" justifyContent="left">
          <FormHelperText>{info}</FormHelperText>
        </Box>
        <Box d="flex" justifyContent="left"></Box>
      </FormControl>
    );
  }

  return (
    <>
      {firstStage.map((field, index) => {
        return renderField(field.name, field, index, false);
      })}
      <AddQuestions
        questions={questions}
        values={values}
        setFieldValue={setFieldValue}
      />
      <Box d="flex" justifyContent="left" m={2}>
        <p>{info}</p>
      </Box>
      {extraFields &&
        Object.entries(extraFields).map((obj, index) => {
          return renderField(obj[0], obj[1], index, true);
        })}

      <Button
        disabled={validate()}
        m={3}
        style={{ float: "right" }}
        variantColor="blue"
        onClick={() => {
          setStage(3);
        }}
      >
        Submit
      </Button>
    </>
  );
}

export default withSnackbar(TaskDetailsForm);
