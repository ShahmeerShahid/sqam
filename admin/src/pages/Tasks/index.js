import React, { useEffect } from "react";
import { Badge, Button, Box, Heading, Skeleton, Stack } from "@chakra-ui/core";
import { withSnackbar } from "notistack";
import { AiOutlinePlusCircle } from "react-icons/ai";
import { Link } from "react-router-dom";
import { useAsync } from "react-use";
import { fetchTasks } from "../../requests/tasks";
import Header from "../../components/Header";
import "./Tasks.css";

// Individual task items tags 
export function TaskItem({ task }) {
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
  let new_Path = "/tasks/";
  if (task.tid || task.tid === 0) {
    new_Path = "/tasks/" + task.tid + "/";
  }

  return (
    <Link to={new_Path}>
      <Box p={5} mb={4} shadow="md" borderWidth="1px">
        <Heading fontSize="xl">
          {task.name}{" "}
          <Badge variantColor={taskColor(task.status)}>{task.status}</Badge>
        </Heading>
      </Box>
    </Link>
  );
}
// get all the task 
export function Tasks({ enqueueSnackbar }) {
  const tasks = useAsync(fetchTasks, []);
  useEffect(() => {
    if (tasks.error) {
      enqueueSnackbar("Failed fetching tasks", { variant: "error" });
    }
  }, [tasks, enqueueSnackbar]);

  return (
    <div>
      <Header />
      <Box mt={4}>
        {/* add task button */}
        <Stack isInline justify="center">
          <h1 className="title">Tasks</h1>
          <Link to="/tasks/add">
            <Button m={2} ml={4} variantColor="green">
              <Box d="flex" justify="center">
                <AiOutlinePlusCircle
                  style={{ size: "2em", marginTop: "2px" }}
                />{" "}
                Add Task
              </Box>
            </Button>
          </Link>
        </Stack>
        {/* Displays all task ran by user */}
        <Box justify="center" w="50%" m="auto">
          <Skeleton isLoaded={!tasks.loading}>
            <Stack spacing={8}>
              {tasks.value ? (
                tasks.value.map((task, index) => (
                  <TaskItem key={index} task={task} />
                ))
              ) : (
                <h1>No tasks to display</h1>
              )}
            </Stack>
          </Skeleton>
        </Box>
      </Box>
    </div>
  );
}

export default withSnackbar(Tasks);
