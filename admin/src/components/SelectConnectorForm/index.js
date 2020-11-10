import React from "react";
import {
  Box,
  Button,
  FormControl,
  FormErrorMessage,
  FormHelperText,
  FormLabel,
  Select,
} from "@chakra-ui/core";
import { useForm } from "react-hook-form";

function SelectConnectorForm({
  connector,
  connectors,
  setConnector,
  setShowAddTaskForm,
}) {
  const { errors, formState, handleSubmit, register, watch } = useForm({
    defaultValues: {
      connector: connector ? connector : 0,
    },
  });
  const watchConnector = watch("connector", 0);
  const onSubmit = async (values) => {
    setConnector(connectors[values.connector]);
    setShowAddTaskForm(true);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <FormControl alignItems="start">
        <Box d="flex" justifyContent="left">
          <FormLabel htmlFor="connector">Connector</FormLabel>
        </Box>
        <Select
          name="connector"
          data-testid="select"
          placeholder="Select connector"
          ref={register({
            required: true,
          })}
        >
          {connectors &&
            connectors.map((connector, index) => (
              <option key={index} value={index} data-testid="select-option">
                {connector.name}
              </option>
            ))}
        </Select>
        <Box d="flex" justifyContent="left">
          <FormHelperText>
            Select the website to pull assessments from
          </FormHelperText>
        </Box>
        <Box d="flex" justifyContent="left">
          <FormErrorMessage>
            {errors.connector && errors.connector.message}
          </FormErrorMessage>
        </Box>
      </FormControl>
      <Button
        variantColor="blue"
        type="submit"
        style={{ float: "right" }}
        m={3}
        isLoading={formState.isSubmitting}
        disabled={watchConnector === 0}
      >
        Submit
      </Button>
    </form>
  );
}

export default SelectConnectorForm;
