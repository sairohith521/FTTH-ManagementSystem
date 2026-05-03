import { Link, useLocation } from "react-router-dom";
import { sidebarItems } from "./sidebarConfig";
import { useAuth } from "../../context/AuthContext";

interface Props {
  collapsed: boolean;
  onToggle: () => void;
}

export default function Sidebar({ collapsed, onToggle }: Props) {
  const { role } = useAuth();
  const location = useLocation();

  return (
    <div
      className={`bg-sidebarBg text-sidebarText flex flex-col py-4 transition-all duration-200 ${
        collapsed ? "w-[60px]" : "w-[220px]"
      }`}
    >
      <div className={`mb-6 flex items-center ${collapsed ? "justify-center" : "px-5 justify-between"}`}>
        {!collapsed && <span className="text-white font-semibold text-[15px]">Aaha FTTH Portal</span>}
        <button
          onClick={onToggle}
          className="text-sidebarText hover:text-white text-lg cursor-pointer bg-transparent border-none"
          title={collapsed ? "Expand" : "Collapse"}
        >
          {collapsed ? "»" : "«"}
        </button>
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
                title={collapsed ? item.label : undefined}
                className={`flex items-center gap-3 py-2.5 text-sm no-underline transition-colors ${
                  collapsed ? "justify-center px-0" : "px-5"
                } ${
                  active
                    ? "bg-sidebarActive text-white font-medium"
                    : "text-sidebarText hover:bg-white/10 hover:text-white"
                }`}
              >
                <span className="text-base">{item.icon}</span>
                {!collapsed && <span>{item.label}</span>}
              </Link>
            );
          })}
      </nav>
    </div>
  );
}
