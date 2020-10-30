import React from "react";
import { Badge, Button, Box, Heading, Skeleton, Stack } from "@chakra-ui/core";
import { AiOutlinePlusCircle } from "react-icons/ai";
import { useAsync } from "react-use";
import { fetchTasks } from "../../requests/tasks";
import NavBar from "../../components/NavBar";
import "./Tasks.css";

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

export function Tasks() {
  const tasks = useAsync(fetchTasks, []);

  return (
    <div>
      <NavBar />
      <Box mt={4}>
        <Stack isInline justify="center">
          <h1 className="title">Tasks</h1>
          <Button mt={2} ml={2} bg="transparent">
            <AiOutlinePlusCircle style={{ size: "2em", color: "green" }} />
          </Button>
        </Stack>
        <Box justify="center" w="50%" m="auto">
          <Skeleton isLoaded={!tasks.loading}>
            <Stack spacing={8}>
              {tasks.value ? (
                tasks.value.map((task, index) => (
                  <Task key={index} task={task} />
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

export default Tasks;
