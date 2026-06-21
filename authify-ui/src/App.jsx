import { useState } from 'react'
import './App.css'
import {ToastContainer} from "react-toastify";
import {Route, Routes} from "react-router-dom";
import ResetPassword from "./pages/ResetPassword.jsx";
import Login from "./pages/Login.jsx";
import Home from "./pages/Home.jsx";
import EmailVerify from "./pages/EmailVerify.jsx";

function App() {
  const [count, setCount] = useState(0)

  return (
   <div>
     <ToastContainer />
     <Routes>
       <Route path="/" element={<Home />} />
       <Route path="/login" element={<Login />} />
       <Route path="/email-verify" element={<EmailVerify />} />
       <Route path="/reset-password" element={<ResetPassword />} />
     </Routes>
   </div>
  )
}

export default App
