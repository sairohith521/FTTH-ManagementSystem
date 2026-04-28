import { Link } from "react-router-dom";
import { sidebarItems } from "./sidebarConfig";
import { currentRole } from "../../utils/mockAuth";

export default function Sidebar() {
  return (
    <div style={{ width: 220, padding: 16 }}>
      {sidebarItems
        .filter(item => item.roles.includes(currentRole))
        .map(item => (
          <div key={item.path}>
            <Link to={item.path}>{item.label}</Link>
          </div>
        ))}
    </div>
  );
}