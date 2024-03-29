package catdice.game.kor;

import net.phys2d.raw.Body;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.svg.SimpleDiagramRenderer;

import catdice.game.kor.struct.AbstractEntity;
import catdice.game.kor.util.VectorUtil;

/**
 * A simple crate showing a default physics body being used. This entity doesn't
 * have the "special" rules applied to it that an Actor does.
 * 
 * @author kevin
 */
public class Scenery extends AbstractEntity {
	/** The image to display for the crate */
	private Image image;
	/** The svg image to display for the thing */
	private SimpleDiagramRenderer svg;

	/** how scaled is it? */
	private float scale;

	/** The width of the crate */
	private float width;
	/** The height of the crate */
	private float height;
	/** The world to which the crate has been added */
	private World world;

	/**
	 * Create a new crate
	 * 
	 * @param x
	 *            The x position of the centre of the crate
	 * @param y
	 *            The y position of the centre of the crate
	 * @param width
	 *            The width of the crate
	 * @param height
	 *            The height of the crate
	 * @param mass
	 *            The mass of the crate
	 * @throws SlickException
	 *             Indicates a failure to load the resources
	 */
	public Scenery(String fileLoc, float x, float y, float width, float height,
			float scale, float mass) throws SlickException {
		this.width = width;
		this.height = height;

		this.scale = scale;

		//		image = new Image("res/crate.png");
		svg = VectorUtil.loadSvg(fileLoc);
		body = new Body(new Box(width, height), mass);
		body.setPosition(x, y);
		body.setRotatable(false);
		body.setMoveable(false);
		body.setFriction(1f);
	}

	/**
	 * @see org.newdawn.penguin.Entity#getBody()
	 */
	public Body getBody() {
		return body;
	}

	/**
	 * @see org.newdawn.penguin.Entity#preUpdate(int)
	 */
	public void preUpdate(int delta) {
	}

	/**
	 * @see org.newdawn.penguin.Entity#render(org.newdawn.slick.Graphics)
	 */
	public void render(Graphics g) {
		// make the thing to its correct scale
		g.scale(scale, scale);
		// move the thing to its correct spot
		g.translate(getX(), getY());
		g.rotate(0, 0, (float) Math.toDegrees(body.getRotation()));

		svg.render(g);
		//
		//		g.rotate(0, 0, (float) -Math.toDegrees(body.getRotation()));
		//		g.translate(-getX(), -getY());
		g.resetTransform();
	}

	/**
	 * @see org.newdawn.penguin.Entity#setWorld(net.phys2d.raw.World)
	 */
	public void setWorld(World world) {
		this.world = world;
	}

	@Override
	public void update(int delta) {
		// TODO Auto-generated method stub

	}

}