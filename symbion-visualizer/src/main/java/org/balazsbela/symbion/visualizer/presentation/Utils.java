package org.balazsbela.symbion.visualizer.presentation;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class Utils {

	public static Quaternion getRotationTo(Vector3f src, Vector3f dest, Vector3f fallbackAxis) {

		// Based on Stan Melax's article in Game Programming Gems
		Quaternion q = new Quaternion();
		// Copy, since cannot modify local
		Vector3f v0 = src;
		Vector3f v1 = dest;
		v0.normalize();
		v1.normalize();

		float d = v0.dot(v1);
		// If dot == 1, vectors are the same
		if (d >= 1.0f) {
			return Quaternion.IDENTITY;
		}
		if (d < (1e-6f - 1.0f)) {
			if (!fallbackAxis.equals(Vector3f.ZERO)) {
				// rotate 180 degrees about the fallback axis
				q.fromAngleAxis(FastMath.PI, fallbackAxis);
			} else {
				// Generate an axis
				Vector3f axis = Vector3f.UNIT_X.cross(src);
				if (axis.length() == 0) // pick another if colinear
					axis = Vector3f.UNIT_Y.cross(src);
				axis.normalize();
				q.fromAngleAxis(FastMath.PI, axis);
			}
		} else {
			double s = Math.sqrt((1 + d) * 2);
			double invs = 1 / s;

			Vector3f c = v0.cross(v1);

			q.set((float) (c.x * invs), (float) (c.y * invs), (float) (c.z * invs), (float) (s * 0.5f));
			q.normalize();
		}
		System.out.println(q.getX()+" "+q.getY()+" "+q.getZ());
		return q;

	}
}
