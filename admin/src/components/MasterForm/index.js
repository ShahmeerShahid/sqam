import React, { useEffect, useState } from "react";
import {
  Button,
  Tabs,
  TabList,
  TabPanels,
  Tab,
  TabPanel,
} from "@chakra-ui/core";
import { saveAs } from "file-saver";
import { withFormik } from "formik";
import { withSnackbar } from "notistack";
import * as Yup from "yup";
import SelectConnectorForm from "../SelectConnectorForm";
import TaskDetailsForm from "../TaskDetailsForm";
import TaskFilesForm from "../TaskFilesForm";
import { fetchExtraFields } from "../../requests/connectors";
import { createTask, uploadTaskFiles } from "../../requests/tasks";

const ERROR_MSGS = {
  nameMissing: "Name is required",
  submissionFileNameMissing: "Submission file is required",
  markingTypeMissing: "Marking type is required",
  dbTypeMissing: "Database type is required",
  maxMarksPerQuestionMissing: "Max marks per question is required",
  questionNamesMissing: "Question names are required",
  extraFieldsMissing: "",
};

const StageOneSchema = Yup.object().shape({
  connector: Yup.object().required(),
  connectorIndex: Yup.number().required(),
  connectorInfo: Yup.object().required(),
});

export const StageTwoSchema = Yup.object().shape({
  name: Yup.string().min(1).required(ERROR_MSGS.nameMissing), // STAGE 2
  submission_file_name: Yup.string()
    .min(1)
    .required(ERROR_MSGS.submissionFileNameMissing),
  marking_type: Yup.string().required(ERROR_MSGS.markingTypeMissing),
  db_type: Yup.string().required(ERROR_MSGS.dbTypeMissing),
  max_marks_per_question: Yup.array()
    .of(Yup.number().min(1))
    .min(1)
    .required(ERROR_MSGS.maxMarksPerQuestionMissing),
  question_names: Yup.array()
    .of(Yup.string())
    .required(ERROR_MSGS.questionNamesMissing),
  extra_fields: Yup.object()
    .shape({
      markus_URL: Yup.string().trim().url().required(),
      assignment_id: Yup.number().min(1).required(),
      api_key: Yup.string()
        .trim()
        .matches(/^[a-zA-Z0-9]*=$/, "API key has invalid format")
        .required(),
    })
    .required(ERROR_MSGS.extraFieldsMissing),
});

const ExcludeFilesSchema = StageOneSchema.concat(StageTwoSchema);

const CreateTaskSchema = ExcludeFilesSchema.concat(
  Yup.object().shape({
    files: Yup.array().required(),
  })
);

function validateStageTwo(values) {
  try {
    StageTwoSchema.validateSync({
      name: values.name,
      marking_type: values.marking_type,
      db_type: values.db_type,
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

function validateStageThree(values) {
  try {
    ExcludeFilesSchema.validateSync({
      connector: values.connector,
      connectorIndex: values.connectorIndex,
      connectorInfo: values.connectorInfo,
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

export function UnconnectedMasterForm({
  connectors,
  enqueueSnackbar,
  handleSubmit,
  resetForm,
  setFieldValue,
  setStage,
  setValues,
  stage,
  values,
}) {
  const [isLoadingConnectorInfo, setIsLoadingConnectorInfo] = useState(false);
  const connector = values.connector;
  const connectorInfo = values.connectorInfo;

  const saveBtn = (
    <Button
      m={3}
      style={{ float: "right" }}
      variant="outline"
      onClick={() => {
        let { files, ...valuesObj } = values;
        var file = new File(
          [JSON.stringify(valuesObj)],
          `task_${new Date()}.json`,
          {
            type: "text/plain;charset=utf-8",
          }
        );
        saveAs(file);
      }}
    >
      Save
    </Button>
  );

  useEffect(() => {
    async function fetchData() {
      setIsLoadingConnectorInfo(true);
      const response = await fetchExtraFields(connector.name);
      if (response.status) {
        enqueueSnackbar("Failed fetching connector information", {
          variant: "error",
        });
      } else {
        setFieldValue("connectorInfo", response);
      }
      setIsLoadingConnectorInfo(false);
    }
    if (connector) fetchData();
  }, [connector, enqueueSnackbar, setFieldValue]);

  return (
    <Tabs
      isFitted
      variant="soft-rounded"
      colorScheme="green"
      align="center"
      index={stage - 1}
    >
      <TabList style={{ paddingBottom: "2vw" }}>
        <Tab onClick={() => setStage(1)}>Connector</Tab>
        <Tab isDisabled={!connector} onClick={() => setStage(2)}>
          Task Details
        </Tab>
        <Tab
          isDisabled={validateStageThree(values)}
          onClick={() => setStage(3)}
        >
          Files
        </Tab>
      </TabList>
      <TabPanels>
        <TabPanel>
          <SelectConnectorForm
            connectors={connectors}
            resetForm={resetForm}
            setFieldValue={setFieldValue}
            setStage={setStage}
            setValues={setValues}
            values={values}
          />
        </TabPanel>
        <TabPanel>
          <TaskDetailsForm
            connectorInfo={connectorInfo}
            isLoadingConnectorInfo={isLoadingConnectorInfo}
            saveBtn={saveBtn}
            setFieldValue={setFieldValue}
            setStage={setStage}
            validate={() => validateStageTwo(values)}
            values={values}
          />
        </TabPanel>
        <TabPanel>
          <TaskFilesForm
            handleSubmit={handleSubmit}
            saveBtn={saveBtn}
            setFieldValue={setFieldValue}
          />
        </TabPanel>
      </TabPanels>
    </Tabs>
  );
}

export const EnhancedMasterForm = withFormik({
  enableReinitialize: false,
  handleSubmit: async (
    {
      connector,
      name,
      marking_type,
      db_type,
      max_marks_per_question,
      question_names,
      submission_file_name,
      extra_fields,
      files,
    },
    { props: { enqueueSnackbar } }
  ) => {
    const max_marks = max_marks_per_question.reduce(function (a, b) {
      return a + b;
    }, 0);

    const uploadRes = await uploadTaskFiles({ files });
    if (uploadRes.error) {
      enqueueSnackbar("Failed uploading files", {
        variant: "error",
      });
    }

    const taskRes = await createTask({
      name,
      marking_type,
      db_type,
      connector: connector.name,
      max_marks,
      max_marks_per_question,
      question_names,
      submission_file_name,
      extra_fields,
    });

    if (taskRes.error) {
      enqueueSnackbar("Failed creating task", {
        variant: "error",
      });
    } else {
      enqueueSnackbar("Task successfully created!", {
        variant: "success",
      });
    }
  },
  mapPropsToValues: (props) => ({
    connector: null,
    connectorIndex: NaN,
    connectorInfo: null,
    name: "",
    submission_file_name: "",
    marking_type: "",
    db_type: "",
    max_marks_per_question: [],
    question_names: [],
    extra_fields: null,
    files: [],
    error: null,
  }),
  validationSchema: () => CreateTaskSchema,
  validateOnBlur: false,
  validateOnChange: false,
})(UnconnectedMasterForm);

export default withSnackbar(EnhancedMasterForm);
