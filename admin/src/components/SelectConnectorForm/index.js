import React, { createRef } from "react";
import {
  Box,
  Button,
  FormControl,
  FormHelperText,
  FormLabel,
  Link,
  Select,
} from "@chakra-ui/core";
import { withSnackbar } from "notistack";
import { CSVReader } from "react-papaparse";
import template from "./questions.csv";

const fileRef = createRef();
// First form for adding task containing option of connector, preloaded configuration, questions 
function SelectConnectorForm({
  connectors,
  enqueueSnackbar,
  resetForm,
  setFieldValue,
  setStage,
  setValues,
  values,
}) {
  const connectorIndex = values.connectorIndex;

  const validateCsv = (rows) => {
    // ensure csv is of correct format
    let i;
    for (i = 0; i < rows.length; i++) {
      try {
        const data = rows[i].data;
        if (data.length !== 2) {
          return false;
        }
        if (i === 0 && (data[0] !== "question" || data[1] !== "marks")) {
          return false;
        } else if (parseInt(data[1]) <= 0) {
          return false;
        }
      } catch (e) {
        return false;
      }
    }
    return true;
  };

  const handleOnDrop = (data) => {
    if (fileRef.current.state.file.name.endsWith(".csv") && validateCsv(data)) {
      setFieldValue(
        "question_names",
        data.slice(1).map((row) => row.data[0])
      );
      setFieldValue(
        "max_marks_per_question",
        data.slice(1).map((row) => parseInt(row.data[1]))
      );
    } else {
      fileRef.current.removeFile();
      enqueueSnackbar("File uploaded must be a .csv of format question,marks", {
        variant: "error",
      });
    }
  };

  return (
    <form>
      <FormControl alignItems="start">
        <Box d="flex" justifyContent="left">
          <FormLabel htmlFor="connector">Connector</FormLabel>
        </Box>
        <Select
          name="connector"
          data-testid="select"
          placeholder="Select connector"
          onChange={(e) =>
            setFieldValue("connectorIndex", parseInt(e.target.value))
          }
          value={
            Number.isNaN(values.connectorIndex) ? "" : values.connectorIndex
          }
        >
          {connectors &&
            connectors.map((connector, index) => (
              <option
                key={index}
                value={index}
                data-testid={`select-option-${index}`}
                style={{ color: "black" }}
              >
                {connector.name}
              </option>
            ))}
        </Select>
        <Box d="flex" justifyContent="left">
          <FormHelperText>
            Select the website to pull assessments from
          </FormHelperText>
        </Box>
      </FormControl>
      <FormControl mt={2}>
        <Box d="flex" justifyContent="left">
          <FormLabel htmlFor="questions">Questions (Optional)</FormLabel>
        </Box>

        <CSVReader ref={fileRef} onDrop={handleOnDrop} addRemoveButton>
          <span>Drop CSV file here or click to upload.</span>
        </CSVReader>
        <Box d="flex" justifyContent="left">
          <FormHelperText>
            Upload a{" "}
            <Link href={template} download className="link">
              csv file
            </Link>{" "}
            for the questions outline
          </FormHelperText>
        </Box>
      </FormControl>
      <FormControl mt={2}>
        <Box d="flex" justifyContent="left">
          <FormLabel htmlFor="config">Configuration (Optional)</FormLabel>
        </Box>
        <Box d="flex" justifyContent="left">
          <input
            type="file"
            onChange={(e) => {
              const fileReader = new FileReader();
              const fileName = e.target.files[0].name;
              if (!fileName.endsWith(".json")) {
                resetForm();
                enqueueSnackbar("Config file uploaded must be a .json", {
                  variant: "error",
                });
                return;
              }
              fileReader.readAsText(e.target.files[0], "UTF-8");
              fileReader.onload = (e) => {
                try {
                  const obj = JSON.parse(e.target.result);
                  setValues(obj);
                } catch (e) {
                  resetForm();
                  enqueueSnackbar("Provided file is incorrectly formatted", {
                    variant: "error",
                  });
                }
              };
            }}
          />
        </Box>
        <Box d="flex" justifyContent="left">
          <FormHelperText>
            Upload a task configuration json file for quick form completion
          </FormHelperText>
        </Box>
      </FormControl>
      <Button
        disabled={isNaN(connectorIndex)}
        isLoading={false}
        m={3}
        style={{ float: "right" }}
        variantColor="blue"
        onClick={() => {
          setFieldValue("connector", connectors[connectorIndex]);
          setStage(2);
        }}
      >
        Continue
      </Button>
    </form>
  );
}

export default withSnackbar(SelectConnectorForm);
