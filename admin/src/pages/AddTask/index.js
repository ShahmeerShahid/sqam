import React, { useEffect, useState } from "react";
import { Box } from "@chakra-ui/core";
import { withSnackbar } from "notistack";
import { useAsync } from "react-use";
import Header from "../../components/Header";
import MasterForm from "../../components/MasterForm";
import { fetchConnectors } from "../../requests/connectors";

function AddTask({ enqueueSnackbar }) {
  const connectors = useAsync(fetchConnectors, []);
  const [stage, setStage] = useState(1);

  useEffect(() => {
    if (connectors.error) {
      enqueueSnackbar("Failed fetching connectors", { variant: "error" });
    }
  }, [connectors, enqueueSnackbar]);

  return (
    <div>
      <Header />
      <Box mt={4} w="100%" d="flex" justifyContent="center">
        <Box w={stage === 1 ? "25%" : "40%"} borderWidth="1px" rounded="lg">
          <Box m={4}>
            <h1 className="title">Add Task</h1>
            <MasterForm
              connectors={connectors.value}
              stage={stage}
              setStage={setStage}
            />
          </Box>
        </Box>
      </Box>
    </div>
  );
}

export default withSnackbar(AddTask);
