import React from "react";
import { Box, List, ListItem, Button, Typography } from "@mui/material";
import { makeStyles } from "@mui/styles";
import { getRoleFromToken } from "../../auth";

const useStyles = makeStyles({
  root: {
    display: "flex",
    height: "100vh",
  },
  navbar: {
    width: "20%",
    backgroundColor: "#f5f5f5",
    display: "flex",
    flexDirection: "column",
    justifyContent: "space-between",
    padding: "16px",
  },
  content: {
    flexGrow: 1,
    overflowY: "auto",
    padding: "20px",
  },
  modLogsContainer: {
    padding: "8px 16px",
  },
  modLogButton: {
    marginBottom: "8px",
  },
});

const Layout = ({ children }) => {
  const classes = useStyles();
  const userRole = getRoleFromToken();

  const handleLogout = () => {
    localStorage.removeItem("token");
    window.location.href = "/login";
  };

  return (
    <Box className={classes.root}>
      {/* Navbar */}
      <Box className={classes.navbar}>
        <List>
          <ListItem>
            <Typography variant="h6">Community Forum</Typography>
          </ListItem>
          <ListItem button>
            <Typography onClick={() => (window.location.href = "/home")}>
              Home
            </Typography>
          </ListItem>
          <ListItem button>
            <Typography onClick={() => (window.location.href = "/user-posts")}>
              My Posts
            </Typography>
          </ListItem>
          {userRole === "moderator" && (
            <ListItem className={classes.modLogsContainer}>
              <Box>
                <Typography variant="subtitle1" sx={{ marginBottom: "8px" }}>
                  Moderator Logs
                </Typography>
                <Button
                  fullWidth
                  variant="outlined"
                  className={classes.modLogButton}
                  onClick={() => (window.location.href = "/mod-logs-comments")}
                >
                  Mod Logs - Comments
                </Button>
                <Button
                  fullWidth
                  variant="outlined"
                  className={classes.modLogButton}
                  onClick={() => (window.location.href = "/mod-logs-posts")}
                >
                  Mod Logs - Posts
                </Button>
                <Button
                  fullWidth
                  variant="outlined"
                  className={classes.modLogButton}
                  onClick={() => (window.location.href = "/mod-logs-actions")}
                >
                  Mod Logs - Actions
                </Button>
              </Box>
            </ListItem>
          )}
        </List>
        <Box>
          <Button
            variant="contained"
            color="primary"
            fullWidth
            onClick={handleLogout}
          >
            Logout
          </Button>
        </Box>
      </Box>

      {/* Main Content */}
      <Box className={classes.content}>{children}</Box>
    </Box>
  );
};

export default Layout;
