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
  Radio,
  RadioGroup,
  Spinner,
  Stack,
} from "@chakra-ui/core";
import { withSnackbar } from "notistack";
import AddQuestions from "../AddQuestions";
import { formatFieldToTitle } from "../../helpers";

// Model of form according to information about the task 
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
  {
    name: "marking_type",
    type: "radio",
    placeholder: "queries.sql",
    info: "",
    required: true,
    options: [
      {
        label: "Partial",
        value: "partial",
      },
      {
        label: "Binary",
        value: "binary",
      },
    ],
  },
  {
    name: "db_type",
    type: "radio",
    placeholder: "queries.sql",
    info: "",
    required: true,
    options: [
      {
        label: "MySQL",
        value: "mysql",
      },
      {
        label: "PostgreSQL",
        value: "postgresql",
      },
    ],
  },
];

// set the input fields for the form 
function InputField({
  name,
  field,
  index,
  isExtraField,
  values,
  handleChange,
}) {
  const type = field.type;
  const placeholder = field.placeholder;
  const info = field.info;
  const options = field.options ? field.options : [];
  let input;
  if (type === "number") {
    input = (
      <NumberInput
        min={0}
        defaultValue={0}
        value={
          isExtraField && values.extra_fields
            ? values.extra_fields[name]
            : values[name]
        }
        onChange={(value) => handleChange(value, name, "number", isExtraField)}
      >
        <NumberInputField />
        <NumberInputStepper>
          <NumberIncrementStepper />
          <NumberDecrementStepper />
        </NumberInputStepper>
      </NumberInput>
    );
  } else if (type === "radio") {
    const value =
      isExtraField && values.extra_fields
        ? values.extra_fields[name]
        : values[name];
    input = (
      <RadioGroup
        name={name}
        onChange={(e) => handleChange(e, name, "string", isExtraField)}
        value={value}
      >
        <Stack direction="row">
          {options.map((option, index) => {
            return (
              <Radio
                key={index}
                value={option.value}
                isChecked={value === option.value}
              >
                {option.label}
              </Radio>
            );
          })}
        </Stack>
      </RadioGroup>
    );
  } else {
    input = (
      <Input
        aria-label={name}
        name={name}
        placeholder={placeholder}
        value={
          isExtraField && values.extra_fields
            ? values.extra_fields[name]
            : values[name]
        }
        onChange={(e) => handleChange(e, name, "string", isExtraField)}
      />
    );
  }

  return (
    <FormControl key={index} mt={4} htmlFor={name}>
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

function TaskDetailsForm({
  connectorInfo,
  isLoadingConnectorInfo,
  questions,
  saveBtn,
  setFieldValue,
  setStage,
  validate,
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

  return (
    <>
      {firstStage.map((field, index) => {
        return (
          // input fields for the information about the task
          <InputField
            key={index}
            name={field.name}
            field={field}
            index={index}
            isExtraField={false}
            values={values}
            handleChange={handleChange}
          />
        );
      })}
      {/* add Question part of the form */}
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
          return (
            <InputField
              key={index}
              name={obj[0]}
              field={obj[1]}
              index={index}
              isExtraField={true}
              values={values}
              handleChange={handleChange}
            />
          );
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
        Continue
      </Button>
      {saveBtn}
    </>
  );
}

export default withSnackbar(TaskDetailsForm);
