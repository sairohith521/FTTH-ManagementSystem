import { Link, useLocation } from "react-router-dom";
import { sidebarItems } from "./sidebarConfig";
import { useAuth } from "../../context/AuthContext";

export default function Sidebar() {
  const { role } = useAuth();
  const location = useLocation();

  return (
    <div className="w-[220px] bg-sidebarBg text-sidebarText flex flex-col py-4">
      <div className="px-5 mb-6">
        <span className="text-white font-semibold text-[15px]">FTTH Admin</span>
      </div>

      <nav className="flex flex-col gap-0.5">
        {sidebarItems
          .filter((item) => item.roles.includes(role))
          .map((item) => {
            const active = location.pathname === item.path;
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`px-5 py-2.5 text-sm no-underline transition-colors ${
                  active
                    ? "bg-sidebarActive text-white font-medium"
                    : "text-sidebarText hover:bg-white/10 hover:text-white"
                }`}
              >
                {item.label}
              </Link>
            );
          })}
      </nav>
    </div>
  );
}
