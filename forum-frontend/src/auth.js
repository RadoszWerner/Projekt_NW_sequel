import { jwtDecode } from "jwt-decode";

export const getUserIdFromToken = () => {
  const token = localStorage.getItem("token");
  if (token) {
    try {
      const decoded = jwtDecode(token);
      return decoded.userId;
    } catch (error) {
      console.error("Invalid token:", error);
      return null;
    }
  }
  return null;
};

export const getRoleFromToken = () => {
  const token = localStorage.getItem("token");
  if (token) {
    try {
      const decoded = jwtDecode(token);
      return decoded.role;
    } catch (error) {
      console.error("Invalid token:", error);
      return null;
    }
  }
  return null;
};

export const getUsernameFromToken = () => {
  const token = localStorage.getItem("token");
  if (token) {
    try {
      const decoded = jwtDecode(token);
      return decoded.sub;
    } catch (error) {
      console.error("Invalid token:", error);
      return null;
    }
  }
  return null;
};
