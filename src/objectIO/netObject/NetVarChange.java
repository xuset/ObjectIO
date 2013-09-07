package objectIO.netObject;

import objectIO.connection.Connection;

public interface NetVarChange {
	public void onChange(NetVar var, Connection c);
}
