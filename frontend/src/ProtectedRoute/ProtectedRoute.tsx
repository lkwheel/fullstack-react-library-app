import { withAuthenticationRequired } from "@auth0/auth0-react";
import React, { ComponentType } from "react";
import { Route, RouteProps } from "react-router-dom";
import { SpinnerLoading } from "../layouts/Utils/SpinnerLoading";

interface ProtectedRouteProps extends RouteProps {
  component: ComponentType;
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  component,
  ...args
}) => (
  <Route
    component={withAuthenticationRequired(component, {
      onRedirecting: () => (
        <div className="page-layout">
          <SpinnerLoading />
        </div>
      ),
    })}
    {...args}
  />
);
