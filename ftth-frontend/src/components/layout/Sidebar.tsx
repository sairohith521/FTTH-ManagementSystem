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
    <div className={`sidebar ${collapsed ? "sidebar--collapsed" : ""}`}>
      <div className="sidebar__header">
        {!collapsed && <span className="sidebar__title">Aaha FTTH Portal</span>}
        <button onClick={onToggle} className="sidebar__toggle" title={collapsed ? "Expand" : "Collapse"}>
          {collapsed ? "«" : "»"}
        </button>
      </div>

      <nav className="sidebar__nav">
        {sidebarItems
          .filter((item) => item.roles.includes(role))
          .map((item) => {
            const active = location.pathname === item.path;
            return (
              <Link
                key={item.path}
                to={item.path}
                title={collapsed ? item.label : undefined}
                className={`sidebar__link ${active ? "sidebar__link--active" : ""}`}
              >
                <span className="sidebar__icon">{item.icon}</span>
                {!collapsed && <span>{item.label}</span>}
              </Link>
            );
          })}
      </nav>
    </div>
  );
}
