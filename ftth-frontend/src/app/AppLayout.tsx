import { useState } from "react";
import { Outlet, Navigate } from "react-router-dom";
import Sidebar from "../components/layout/Sidebar";
import Header from "../components/layout/Header";
import { useAuth } from "../context/AuthContext";
import { PageTitleProvider } from "../context/PageTitleContext";

export default function AppLayout() {
  const { isLoggedIn } = useAuth();
  const [collapsed, setCollapsed] = useState(false);

  if (!isLoggedIn) return <Navigate to="/login" />;

  return (
    <PageTitleProvider>
      <div className="app-layout">
        <Sidebar collapsed={collapsed} onToggle={() => setCollapsed((c) => !c)} />
        <div className="app-right">
          <Header />
          <main className="app-main">
            <Outlet />
          </main>
        </div>
      </div>
    </PageTitleProvider>
  );
}
