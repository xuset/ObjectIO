package objectIO.netObject;

import objectIO.connections.Connection;

public interface NetVarChange {
	public void onChange(NetVar var, Connection c);
}
