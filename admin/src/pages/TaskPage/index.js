import React, { useEffect } from "react";
import { Badge, Box, Heading, ThemeProvider, CSSReset, theme, Text, Grid, Flex } from "@chakra-ui/core";
import { withSnackbar } from "notistack";
import { Link, useParams } from "react-router-dom";
import { useAsync } from "react-use";
import Header from "../../components/Header";
import { fetchTasks } from "../../requests/tasks";
import "./TaskPage.css";
import TaskDetail from "../../components/TaskDetail";
import TaskLog from "../../components/TaskLog";

export function Task({ task }) {
  const taskColor = (status) => {
    switch (status) {
      case "Complete":
        return "green";
      case "Pending":
        return "yellow";
      case "Error":
        return "red";
      case "Marking":
        return "red";
      default:
        return "";
    }
  };

  return (
    <Box p={5} mb={4} shadow="md" borderWidth="1px">
      <Heading fontSize="xl">
        {task.name}{" "}
        <Badge variantColor={taskColor(task.status)}>{task.status}</Badge>
      </Heading>
    </Box>
  );
}

export function TaskPage({ enqueueSnackbar }) {
  const tasks = useAsync(fetchTasks, []);
  let { tid } = useParams();
  useEffect(() => {
    if (tasks.error) {
      enqueueSnackbar("Failed fetching tasks", { variant: "error" });
    }
  }, [tasks, enqueueSnackbar]);

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
              Welcome to Task({tid})
            </Text>
          </Flex>
        </Flex>
        <Grid p={10} gap={6} templateColumns="repeat(auto-fit, minmax(350px, 1fr))">
          <TaskDetail tid={tid} />
          <TaskLog tid={tid} />
        </Grid>
      </ThemeProvider>
    </div>
  );
}

export default withSnackbar(TaskPage);
