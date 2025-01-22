import React, { useEffect, useState } from "react";
import { getUserIdFromToken } from "../../auth";
import PostList from "../Posts/PostList";

const UserPostsPage = () => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const userId = getUserIdFromToken();

    if (userId) {
      fetch(`http://localhost:8080/posts/user/${userId}`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
          "Content-Type": "application/json",
        },
      })
        .then((response) => response.json())
        .then((data) => {
          setPosts(data);
          console.log(data);
          setLoading(false);
        })
        .catch((error) => {
          console.error("Error fetching user's posts:", error);
          setLoading(false);
        });
    } else {
      console.error("No valid userId found.");
      setLoading(false);
    }
  }, []);

  return <div>{loading ? <p>Loading...</p> : <PostList posts={posts} />}</div>;
};

export default UserPostsPage;
