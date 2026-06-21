import {Link, useNavigate} from "react-router-dom";
import assets from "../assets/assets.js";
import {useContext, useState} from "react";
import axios from "axios";
import {AppContext} from "../context/AppContext.jsx";
import {toast} from "react-toastify";

const Login = () => {

    const [isCreateAccount, setIsCreateAccount] = useState(false);
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const {backendUrl,setIsLoggedIn,getUserData} = useContext(AppContext);
    const navigate = useNavigate();



    const onSubmitHandler = async (e) => {
        e.preventDefault();


        if (!email?.trim()) {
            toast.error("Email is required.");
            return;
        }
        if (!password?.trim()) {
            toast.error("Password is required.");
            return;
        }

        axios.defaults.withCredentials = true;
        setLoading(true);


        try {
            if (isCreateAccount) {
                //todo: call register api

                if (!firstName?.trim()) {
                    toast.error("First name is required.");
                    return;
                }
                if (!lastName?.trim()) {
                    toast.error("Last name is required.");

                    return;
                }

                const response = await axios.post(`${backendUrl}/register`, {firstName, lastName, email, password});
                if (response.status === 201) {
                    navigate("/");
                    toast.success("Account created successfully.");
                } else {
                    toast.error("Something went wrong.");
                }
            } else {
                //todo: call login api
                 const response= await axios.post(`${backendUrl}/login`, {email, password});

                 if (response.status === 200) {
                     setIsLoggedIn(true);
                     getUserData();
                     navigate("/");
                 } else {
                     toast.error(response.data);
                 }


            }
        } catch (error) {
            console.log(error.response);
            toast.error(error.response.data.message);
        } finally {
            setLoading(false);
        }


    }

    return (
        <div className="position-relative min-vh-100 d-flex justify-content-center align-items-center"
             style={{background: "linear-gradient(90deg, #6a5af9,#8268f9)", border: "none"}}>
            <div style={{position: "absolute", top: "20px", left: "30px", display: "flex", alignItems: "center"}}>
                <Link to="/"
                      style={{
                          display: "flex",
                          gap: 5,
                          alignItems: "center",
                          fontWeight: "bold",
                          fontSize: "24px",
                          textDecoration: "none",
                      }}>
                    <img src={assets.logo} width={32} height={32} alt="logo"/> <span className="fw-bold fs-4 text-light">Authify</span>
                </Link>
            </div>

            {/*   login form*/}
            <div className="card py-4 px-4" style={{maxWidth: "400px", width: "100%"}}>
                <h2 className="text-center mb-4">
                    {isCreateAccount ? "Create Account" : "Login"}
                </h2>
                <form onSubmit={onSubmitHandler}>
                    {
                        isCreateAccount && (
                            <div className="mb-3">
                                <label htmlFor="firstName" className="form-label">First Name</label>
                                <input type="text"
                                       id="firstName"
                                       className="form-control"
                                       placeholder="Enter First Name"
                                       required
                                       onChange={(e) => setFirstName(e.target.value)}
                                       value={firstName}
                                />
                            </div>
                        )
                    }

                    {
                        isCreateAccount && (
                            <div className="mb-3">
                                <label htmlFor="lastName" className="form-label">Last Name</label>
                                <input type={"text"}
                                       id="lastName"
                                       className="form-control"
                                       placeholder="Enter Last Name"
                                       required
                                       onChange={(e) => setLastName(e.target.value)}
                                       value={lastName}
                                />
                            </div>
                        )
                    }


                    <div className="mb-3">
                        <label htmlFor="email" className="form-label">Email</label>
                        <input type="email"
                               id="email"
                               className="form-control"
                               placeholder="Enter Email"
                               required
                               onChange={(e) => setEmail(e.target.value)}
                               value={email}
                        />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="password" className="form-label">Password</label>
                        <input type="password"
                               id="password"
                               className="form-control"
                               placeholder="**********"
                               required
                               onChange={(e) => setPassword(e.target.value)}
                               value={password}
                        />
                    </div>


                    {
                        !isCreateAccount && (
                            <div className="d-flex justify-content-between mb-3">

                                <Link to="/reset-password" className="text-decoration-none"
                                      style={{fontWeight: "bold", color: "red"}}>
                                    Forgot Password?
                                </Link>
                            </div>
                        )
                    }


                    <button type="submit" className="btn btn-primary w-100" disabled={loading}
                            onClick={onSubmitHandler}>
                        {loading ? "Loading..." : isCreateAccount ? "Sign Up" : "Login"}
                    </button>

                    <div className="text-center mt-3">
                        <p className="mb-0">
                            {isCreateAccount ?
                                (
                                    <>
                                        Already have an account? {" "}
                                        <span onClick={() => setIsCreateAccount(false)}
                                              className="text-decoration-underline" style={{cursor: "pointer"}}>
                                            Login Here
                                        </span>
                                    </>
                                ) : (
                                    <>
                                        Don't have an account? {" "}
                                        <span onClick={() => setIsCreateAccount(true)}
                                              className="text-decoration-underline" style={{cursor: "pointer"}}>
                                            Sign Up
                                        </span>
                                    </>
                                )
                            }
                        </p>
                    </div>

                </form>

            </div>

        </div>
    )
}

export default Login;