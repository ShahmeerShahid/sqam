import React from "react";
import {
  Box,
  Button,
  FormControl,
  FormErrorMessage,
  FormHelperText,
  FormLabel,
  Input,
} from "@chakra-ui/core";
import { withSnackbar } from "notistack";
import { useForm } from "react-hook-form";
import { useHistory } from "react-router-dom";
import { createTask } from "../../requests/tasks";
import {
  formatFieldToTitle,
  validateNumber,
  validateMarkUsAPIKey,
} from "../../helpers";

function getPattern(name, info, type) {
  if (type === "number") {
    return validateNumber;
  } else if (name === "api_key" && info.includes("Markus")) {
    return validateMarkUsAPIKey;
  }
  return null;
}

function renderExtraField(errors, extraField, key, name, register) {
  // Unfortunately chakra-ui's NumberInput does not work with react-hook-form :(
  return (
    <FormControl key={key} isInvalid={errors[name]} mt={4} for={name}>
      <Box d="flex" justifyContent="left">
        <FormLabel htmlFor={name}>{formatFieldToTitle(name)}</FormLabel>
      </Box>
      <Input
        name={name}
        aria-label={name}
        placeholder={extraField.placeholder}
        ref={register({
          required: `${formatFieldToTitle(name)} is required`,
          pattern: getPattern(name, extraField.info, extraField.type),
        })}
      />

      <Box d="flex" justifyContent="left">
        <FormHelperText>{extraField.info}</FormHelperText>
      </Box>

      <Box d="flex" justifyContent="left">
        <FormErrorMessage>
          {errors[name] && errors[name].message}
        </FormErrorMessage>
      </Box>
    </FormControl>
  );
}

function AddTaskForm({
  info,
  connector,
  enqueueSnackbar,
  extraFields,
  setShowAddTaskForm,
}) {
  let history = useHistory();

  const { errors, formState, handleSubmit, register } = useForm();
  const onSubmit = async (values) => {
    const omitNameFromValues = (({ taskName, ...o }) => o)(values);
    const response = await createTask({
      name: values.taskName,
      connector: connector.url.slice(7),
      extra_fields: omitNameFromValues,
    });
    if (response.error) {
      enqueueSnackbar(
        "An error occurred while adding a task, please try again.",
        {
          variant: "error",
        }
      );
    } else {
      enqueueSnackbar("Task added!", {
        variant: "success",
      });
      history.push("/tasks");
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <Box d="flex" justifyContent="left">
        <p>{info}</p>
      </Box>
      <FormControl isInvalid={errors.taskName} for="taskName">
        <Box d="flex" justifyContent="left">
          <FormLabel htmlFor="taskName">Task Name</FormLabel>
        </Box>
        <Input
          name="taskName"
          placeholder="CSC343 A1"
          aria-labelledby="taskName"
          ref={register({ required: "Task name is required" })}
        />
        <Box d="flex" justifyContent="left">
          <FormErrorMessage>
            {errors.taskName && errors.taskName.message}
          </FormErrorMessage>
        </Box>
      </FormControl>
      {extraFields &&
        Object.entries(extraFields).map((obj, index) => {
          return renderExtraField(errors, obj[1], index, obj[0], register);
        })}
      <Box d="flex" justifyContent="space-between">
        <Button m={3} onClick={() => setShowAddTaskForm(false)}>
          Back
        </Button>
        <Button
          variantColor="blue"
          type="submit"
          style={{ float: "right" }}
          m={3}
          isLoading={formState.isSubmitting}
        >
          Submit
        </Button>
      </Box>
    </form>
  );
}

export default withSnackbar(AddTaskForm);
