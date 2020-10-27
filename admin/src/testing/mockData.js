export const createMockTask = ({ id, name, status }) => ({
  id,
  name,
  status,
});

export const mockTasks = [
  createMockTask({
    id: 1,
    name: "Task 1",
    status: "Pending",
  }),
  createMockTask({
    id: 2,
    name: "Task 2",
    status: "Marking",
  }),
];
