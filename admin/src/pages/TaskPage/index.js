import React, {useEffect} from "react";
import { ThemeProvider, Text, Grid, Flex } from "@chakra-ui/core";
import { withSnackbar } from "notistack";
import { useParams } from "react-router-dom";
import Header from "../../components/Header";
import "./TaskPage.css";
import TaskDetail from "../../components/TaskDetail";
import TaskLog from "../../components/TaskLog";
import { useAsync } from "react-async"
import { fetchTasksInfo } from "../../requests/tasks";


export function TaskPage({ enqueueSnackbar }) {
  let { tid } = useParams();
  let tasks_info = useAsync({promiseFn: fetchTasksInfo, tid: tid });
  let TaskData = tasks_info.data
  let TaskLogs = []
  
  useEffect(() => {
      if (tasks_info.error) {
          enqueueSnackbar("Failed fetching task information", { variant: "error" });
      }
      TaskData = tasks_info.data
  }, [tasks_info, enqueueSnackbar]);
  if (TaskData){
    TaskLogs = TaskData.logs
    // Dummy Data
    // TaskLogs = [{"timestamp":"Monday", "text": " Line 23:18:  Assignments to the 'TaskData' variable from inside React Hook useEffect will be lost after each render. To preserve the value over time, store it in a useRef Hook and keep the mutable value in the '.current' property. Otherwise, you can move this variable directly inside useEffect  react-hooks/exhaustive-deps", "source":"automarker"}, {"timestamp":"Tuesday", "text": " not Working Hard", "source":"connector"}]
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
        <Grid p={10} gap={6} templateColumns="repeat(auto-fit, minmax(350px, 1fr))">
          <TaskDetail TaskData={TaskData} tid={tid} enqueueSnackbar={enqueueSnackbar}/>
          <TaskLog TaskLogs={TaskLogs} tid={tid} enqueueSnackbar={enqueueSnackbar}/>
        </Grid>
      </ThemeProvider>
    </div>
  );
}

export default withSnackbar(TaskPage);
