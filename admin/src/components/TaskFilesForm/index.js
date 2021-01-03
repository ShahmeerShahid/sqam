import React, { useState } from "react";
import {
  Box,
  Button,
  Checkbox,
  CheckboxGroup,
  FormLabel,
  Stack,
} from "@chakra-ui/core";
import Dropzone from "react-dropzone-uploader";
import { withSnackbar } from "notistack";
import { Link } from "react-router-dom";

const Layout = ({
  input,
  previews,
  dropzoneProps,
  files,
  extra: { maxFiles },
}) => {
  return (
    <div>
      {previews}

      <div {...dropzoneProps}>{files.length < maxFiles && input}</div>
    </div>
  );
};

const FileDropzone = ({
  enqueueSnackbar,
  requiredFiles,
  isChecked,
  setFieldValue,
  setIsChecked,
}) => {
  const handleChangeStatus = (data, status, files) => {
    const name = data.meta.name;
    const arr = name.split(".");
    const num_checked = isChecked.filter(Boolean).length;
    if (status !== "removed" && !requiredFiles.includes(arr[0])) {
      // unauthorized file case
      data.remove();
    } else if (
      status === "preparing" &&
      requiredFiles.includes(arr[0]) &&
      isChecked[requiredFiles.indexOf(arr[0])]
    ) {
      // file reupload case
      let i;
      for (i = 0; i < files.length; i++) {
        if (files[i].meta.name === name) {
          files[i].remove();
          enqueueSnackbar(`File ${name} reuploaded`, {
            variant: "success",
          });
          setFieldValue("files", files);
          return;
        }
      }
    } else if (
      status === "preparing" ||
      (status === "removed" && files.length === num_checked)
    ) {
      const newIsChecked = [...isChecked];
      const index = requiredFiles.indexOf(arr[0]);
      newIsChecked[index] = !newIsChecked[index];
      setFieldValue("files", files);
      setIsChecked(newIsChecked);
    }
  };

  const handleSubmit = (files, allFiles) => {
    allFiles.forEach((f) => f.remove());
  };

  return (
    <Dropzone
      LayoutComponent={Layout}
      onChangeStatus={handleChangeStatus}
      onSubmit={handleSubmit}
      inputContent="Upload Files"
    />
  );
};

function TaskFilesForm({
  enqueueSnackbar,
  handleSubmit,
  saveBtn,
  setFieldValue,
}) {
  const requiredFiles = [
    "create_tables",
    "create_trigger",
    "create_function",
    "load_data",
    "solutions",
  ];
  const [isChecked, setIsChecked] = useState([
    false,
    false,
    false,
    false,
    false,
  ]);

  const hasSubmittedAllFiles = () => {
    let i;
    for (i = 0; i < isChecked.length; i++) {
      if (!isChecked[i]) {
        return false;
      }
    }
    return true;
  };

  return (
    <>
      <Box d="flex" justifyContent="left">
        <FormLabel>Required Files</FormLabel>
      </Box>
      <CheckboxGroup colorScheme="green" defaultValue={["create_tables"]}>
        <Stack>
          {requiredFiles.map((file, index) => (
            <Checkbox
              key={index}
              value={file}
              isChecked={isChecked[index]}
              isReadOnly
            >{`${file}.sql`}</Checkbox>
          ))}
        </Stack>
      </CheckboxGroup>
      <FileDropzone
        enqueueSnackbar={enqueueSnackbar}
        requiredFiles={requiredFiles}
        isChecked={isChecked}
        setFieldValue={setFieldValue}
        setIsChecked={setIsChecked}
      />
      <Button
        disabled={!hasSubmittedAllFiles()}
        m={3}
        style={{ float: "right" }}
        variantColor="blue"
        onClick={handleSubmit}
      >
        <Link to="/tasks">Submit</Link>
      </Button>
      {saveBtn}
    </>
  );
}

export default withSnackbar(TaskFilesForm);
