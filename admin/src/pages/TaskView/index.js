import React, { useEffect } from "react";
import { Button, Text, Grid, Flex } from "@chakra-ui/core";
import { withSnackbar } from "notistack";
import { useParams } from "react-router-dom";
import Header from "../../components/Header";
import TaskDetail from "../../components/TaskDetail";
import TaskLog from "../../components/TaskLog";
import { useAsync } from "react-async";
import { downloadReport, fetchTasksInfo } from "../../requests/tasks";

function TaskView({ enqueueSnackbar }) {
  let { tid } = useParams();
  // get the task information
  let tasks_info = useAsync({ promiseFn: fetchTasksInfo, tid: tid });
  let taskData = tasks_info.data;
  let taskLogs = [];

  useEffect(() => {
    if (tasks_info.error) {
      enqueueSnackbar("Failed fetching task information", { variant: "error" });
    }
  }, [tasks_info, enqueueSnackbar]);

  // Separate the logs from the TaskData
  if (taskData) {
    taskLogs = taskData.logs;
  }

  // function to handle download of report of marked submissions of a task 
  async function handleDownload() {
    const response = await downloadReport(tid);
    if (response.error) {
      enqueueSnackbar("Failed downloading report", { variant: "error" });
    } else {
      const link = document.createElement("a");
      link.href = URL.createObjectURL(
        new Blob([response.data], { type: "application/zip" })
      );
      link.value = "download";
      link.download = `task${tid}-report.zip`;
      link.click();
    }
  }
  return (
    <div>
      <Header />
      <Flex
        display="flex"
        flexDirection="column"
        alignItems="center"
        justifyContent="center"
        textAlign="center"
        mt={4}
      >
        <Flex
          display="flex"
          flexDirection="row"
          alignItems="flex-start"
          justifyContent="flex-start"
        >
          <Text fontSize="3xl" fontWeight="bold">
            Task({tid})
          </Text>
          {taskData && taskData.status === "Complete" && (
            <Button
              onClick={handleDownload}
              style={{ float: "right" }}
              m={1}
              ml={4}
            >
              Download Report
            </Button>
          )}
        </Flex>
      </Flex>
      <Grid
        p={10}
        gap={6}
        templateColumns="repeat(auto-fit, minmax(350px, 1fr))"
      >
        {/* Displays information about the task */}
        <TaskDetail
          taskData={taskData}
          tid={tid}
          enqueueSnackbar={enqueueSnackbar}
        />
        {/* Displays the logs for each services running for the marking job*/}
        <TaskLog
          taskLogs={taskLogs}
          tid={tid}
          enqueueSnackbar={enqueueSnackbar}
        />
      </Grid>
    </div>
  );
}

export default withSnackbar(TaskView);
