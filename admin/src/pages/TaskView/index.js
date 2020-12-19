import React, { useEffect } from "react";
import { ThemeProvider, Text, Grid, Flex } from "@chakra-ui/core";
import { withSnackbar } from "notistack";
import { useParams } from "react-router-dom";
import Header from "../../components/Header";
import TaskDetail from "../../components/TaskDetail";
import TaskLog from "../../components/TaskLog";
import { useAsync } from "react-async";
import { fetchTasksInfo } from "../../requests/tasks";

export function TaskView({ enqueueSnackbar }) {
  let { tid } = useParams();
  let tasks_info = useAsync({ promiseFn: fetchTasksInfo, tid: tid });
  let TaskData = tasks_info.data;
  let TaskLogs = [];

  useEffect(() => {
    if (tasks_info.error) {
      enqueueSnackbar("Failed fetching task information", { variant: "error" });
    }
  }, [tasks_info, enqueueSnackbar]);
  
  if (TaskData) {
    TaskLogs = TaskData.logs;
  }

  return (
    <div>
      <Header />
      <ThemeProvider>
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
          </Flex>
        </Flex>
        <Grid
          p={10}
          gap={6}
          templateColumns="repeat(auto-fit, minmax(350px, 1fr))"
        >
          <TaskDetail
            TaskData={TaskData}
            tid={tid}
            enqueueSnackbar={enqueueSnackbar}
          />
          <TaskLog
            TaskLogs={TaskLogs}
            tid={tid}
            enqueueSnackbar={enqueueSnackbar}
          />
        </Grid>
      </ThemeProvider>
    </div>
  );
}

export default withSnackbar(TaskView);
