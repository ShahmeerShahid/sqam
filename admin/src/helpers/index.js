export function formatFieldToTitle(field) {
  if (!field.includes("_")) {
    return "";
  }
  const words = field.split("_");
  var title = "";
  words.forEach((word) => {
    title = title + word[0].toUpperCase() + word.slice(1) + " ";
  });
  return title.trim();
}

export const validateNumber = {
  value: /^\d+$/,
  message: "Must be a non-negative number",
};

export const validateMarkUsAPIKey = {
  value: /^[a-zA-Z0-9]*=$/,
  message: "Must be a valid MarkUs API key",
};
