// formats the field into title format -> convert '_' to space and uppercase first character
export function formatFieldToTitle(field) {
  if (!field) {
    return "";
  } else if (!field.includes("_")) {
    return field[0].toUpperCase() + field.slice(1);
  }
  const words = field.split("_");
  var title = "";
  words.forEach((word) => {
    title = title + word[0].toUpperCase() + word.slice(1) + " ";
  });
  return title.trim();
}

// Markus api key regex to check for '=' at the end of key
export const validateMarkUsAPIKey = {
  value: /^[a-zA-Z0-9]*=$/,
  message: "Must be a valid MarkUs API key",
};
