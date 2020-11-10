import React, { useEffect, useState } from "react";
import { Box } from "@chakra-ui/core";
import { withSnackbar } from "notistack";
import { useAsync } from "react-use";
import Header from "../../components/Header";
import AddTaskForm from "../../components/AddTaskForm";
import SelectConnectorForm from "../../components/SelectConnectorForm";
import { fetchConnectors, fetchExtraFields } from "../../requests/connectors";

function AddTask({ enqueueSnackbar }) {
  const [connector, setConnector] = useState(null);
  const [connectorInfo, setConnectorInfo] = useState({});
  const [showAddTaskForm, setShowAddTaskForm] = useState(false);
  const connectors = useAsync(fetchConnectors, []);
  useEffect(() => {
    if (connectors.error) {
      enqueueSnackbar("Failed fetching connectors", { variant: "error" });
    }
  }, [connectors, enqueueSnackbar]);

  useEffect(() => {
    async function fetchData() {
      const response = await fetchExtraFields(connector.port);
      if (response.status) {
        enqueueSnackbar("Failed fetching connector information", {
          variant: "error",
        });
      } else {
        setConnectorInfo(response);
      }
    }
    if (connector) fetchData();
  }, [connector, enqueueSnackbar, setConnectorInfo]);

  return (
    <div>
      <Header />
      <Box mt={4} w="100%" d="flex" justifyContent="center">
        <Box w="25%" borderWidth="1px" rounded="lg">
          <Box m={4}>
            <h1 className="title">Add Task</h1>
            {!showAddTaskForm ? (
              <SelectConnectorForm
                connector={connector}
                connectors={connectors.value ? connectors.value : []}
                setConnector={setConnector}
                setShowAddTaskForm={setShowAddTaskForm}
              />
            ) : (
              <AddTaskForm
                info={connectorInfo.info}
                extraFields={
                  connectorInfo.extra_fields ? connectorInfo.extra_fields : null
                }
                setShowAddTaskForm={setShowAddTaskForm}
              />
            )}
          </Box>
        </Box>
      </Box>
    </div>
  );
}

export default withSnackbar(AddTask);
