interface UserInfo {
  app_metadata: {
    role: string;
  };
}

interface Permission {
  name: string;
}

export default class AuthUtils {
  // Function to get user's role and permissions
  static async getUserRoleAndPermissions(email: string, accessToken: string): Promise<Permission[]> {
    try {
      // Step 1: Get user information from Auth0
      const userInfo = await this.getUserInfo(email, accessToken);

      // Step 2: Extract the user's role
      const userRole = userInfo.app_metadata.role;

      // Step 3: Get permissions associated with the user's role
      const permissions = await this.getPermissions(userRole, accessToken);
      return permissions;
    } catch (error) {
      console.error(error);
      throw new Error("Failed to retrieve user role and permissions");
    }
  }

  // Function to get user information from Auth0
  private static async getUserInfo(email: string, accessToken: string): Promise<UserInfo> {
    try {
      const response = await fetch(`https://${process.env.REACT_APP_AUTH0_DOMAIN}/api/v2/users-by-email?email=${email}`, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      const data = await response.json();
      return data[0]; // Assuming there's only one user with this email
    } catch (error) {
      throw new Error("Failed to retrieve user information from Auth0");
    }
  }

  // Function to get permissions associated with a role
  private static async getPermissions(roleId: string, accessToken: string): Promise<Permission[]> {
    try {
      const response = await fetch(`https://${process.env.REACT_APP_AUTH0_DOMAIN}/api/v2/roles/${roleId}/permissions`, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      const data = await response.json();
      return data;
    } catch (error) {
      throw new Error("Failed to retrieve permissions for the role");
    }
  }

  // Function to get the Management API access token
  static async getManagementApiAccessToken() {
    try {
      const response = await fetch(`https://${process.env.REACT_APP_AUTH0_DOMAIN}/oauth/token`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          client_id: process.env.AUTH0_M2M_CLIENT_ID,
          client_secret: process.env.AUTH0_M2M_CLIENT_SECRET,
          audience: process.env.REACT_APP_AUTH0_AUDIENCE,
          grant_type: 'client_credentials'
        })
      });

      const data = await response.json();
      return data.access_token;
    } catch (error) {
      throw new Error('Failed to obtain Management API access token');
    }
  }
}
