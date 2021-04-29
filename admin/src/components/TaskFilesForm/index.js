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
    const fileName = data.meta.name;
    const num_checked = isChecked.filter(Boolean).length;
    if (status !== "removed" && !requiredFiles.includes(fileName)) {
      // unauthorized file case
      data.remove();
    } else if (
      status === "preparing" &&
      requiredFiles.includes(fileName) &&
      isChecked[requiredFiles.indexOf(fileName)]
    ) {
      // file reupload case
      let i;
      for (i = 0; i < files.length; i++) {
        if (files[i].meta.name === fileName) {
          files[i].remove();
          enqueueSnackbar(`File ${fileName} reuploaded`, {
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
      const index = requiredFiles.indexOf(fileName);
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
  const requiredFiles = ["init.sql", "solutions.sql"];
  const [isChecked, setIsChecked] = useState([false]);

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
            >{`${file}`}</Checkbox>
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
