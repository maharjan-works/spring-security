import Header from "../components/Header.jsx";
import Menubar from "../components/Menubar.jsx";

const Home = () => {
    return (
     <div className="flex flex-col justify-items-center justity-content-center min-vh-100">
         <Menubar />
         <Header />
     </div>
    )
}

export default Home;