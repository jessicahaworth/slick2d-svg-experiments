package catdice.game.kor;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import catdice.game.kor.struct.Environment;

/**
 * Simple platform single state to render the tile map and the entities, update
 * the physical world and allow player input
 * 
 * @author kevin
 */
public class GameState extends BasicGameState {
	/** The unique ID given to the state */
	private static final int ID = 1;
	private static int width;
	private static int height;

	/** The environment in which the physics demo is taking place */
	private Environment env;
	/** The player that is being controlled */
	private Actor player;
	/** The view x-axis offset */
	private float xoffset;
	/** The view y-axis offset */
	private float yoffset;
	/** The background image to be displayed */
	private Image background;

	/** The amount of time passed since last control check */
	private int totalDelta;
	/** The interval to check the controls at */
	private int controlInterval = 50;
	/** True if we're showing the bounds of the environment's shapes */
	private boolean showBounds = true;

	/**
	 * @see org.newdawn.slick.state.BasicGameState#getID()
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @see org.newdawn.slick.state.GameState#init(org.newdawn.slick.GameContainer,
	 *      org.newdawn.slick.state.StateBasedGame)
	 */
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		width = container.getWidth();
		height = container.getHeight();
		background = new Image(width, height);
		restart();
	}

	/**
	 * Restart the demo
	 * 
	 * @throws SlickException
	 *             Indicates a failure to load resources
	 */
	private void restart() throws SlickException {
		// TileSet set = new TileSet("res/tiles.xml");
		// MapLoader loader = new MapLoader(set);
		VectorEnvironment env = new VectorEnvironment(width, height);// loader.load("res/testmap.txt");
		env.init();

		/*
		 * string location of the file of the svg image 
		 * x spawning location of bbox (and image?) 
		 * y spawning location of bbox (and image?) 
		 * how scaled the vector image is 
		 * how "heavy" the.. bbox? is? 
		 * the diameter of the bounding box
		 */

		player = new Thing("data/svg/pinkSquare.svg", width / 2, height / 2,
			100, 60);
		Log.info("width is " + width);

		//		Thing testThing = new Thing("data/svg/orc.svg", 0, 0, 100, 5);

		//Create ground object
		Scenery ground = new Scenery("data/svg/griffin.svg", width / 2, height,
			width, 1, .0008f, 2);

		env.addEntity(player);
		//		env.addEntity(testThing);
		env.addEntity(ground);
		this.env = env;
	}

	/**
	 * @see org.newdawn.slick.state.GameState#render(org.newdawn.slick.GameContainer,
	 *      org.newdawn.slick.state.StateBasedGame, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.setClip(0, 0, width, height);
		//		float width = container.getWidth();
		//		float height = container.getHeight();
		float backPar = 3f; //TODO what is this?
		float bx = ((-xoffset * backPar) % width) / -width;
		float by = ((-yoffset * backPar) % height) / -height;
		background.bind();
		g.setColor(Color.white);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(bx, by);
		GL11.glVertex2f(0, 0);
		GL11.glTexCoord2f(bx + 3, by);
		GL11.glVertex2f(width, 0);
		GL11.glTexCoord2f(bx + 3, by + 3);
		GL11.glVertex2f(width, height);
		GL11.glTexCoord2f(bx, by + 3);
		GL11.glVertex2f(0, height);
		GL11.glEnd();

		g.translate(-(int) xoffset, -(int) yoffset);

		env.render(g);
		if (showBounds) {
			env.renderBounds(g);
		}

		g.translate((int) xoffset, (int) yoffset);

		drawString(g, "Game Test", 0);
		drawString(g,
			"Cursors - Move   Ctrl - Jump   B - Show Bounds   R - Restart",
			(int) (height - 20));

		g.setColor(Color.white);
	}

	/**
	 * Draw a clear string centered horizontally
	 * 
	 * @param g
	 *            The graphics context on which to draw the string
	 * @param str
	 *            The string to draw
	 * @param y
	 *            The vertical location to draw at
	 */
	private void drawString(Graphics g, String str, int y) {
		int x = (width - g.getFont().getWidth(str)) / 2;

		g.setColor(Color.blue); // cool "shadow" text
		g.drawString(str, x + 1, y + 1);
		g.setColor(Color.white);
		g.drawString(str, x, y);

	}

	/**
	 * @see org.newdawn.slick.state.GameState#update(org.newdawn.slick.GameContainer,
	 *      org.newdawn.slick.state.StateBasedGame, int)
	 */
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		Input input = container.getInput();

		// the forces applied for different actions. The move force is applied
		// over and over so is reasonably small. The jump force is a one shot
		// deal and so is reasonably big
		float moveForce = 200;
		float jumpForce = 8000000;

		// restart and bounds toggling
		if (input.isKeyPressed(Input.KEY_R)) {
			restart();
			return;
		}
		if (input.isKeyPressed(Input.KEY_B)) {
			showBounds = !showBounds;
		}

		if (input.isKeyDown(Input.KEY_LEFT)) {
			player.applyForce(-moveForce, 0);
		}
		if (input.isKeyDown(Input.KEY_UP)) {
			player.applyForce(0, -jumpForce / 10);
		}
		if (input.isKeyDown(Input.KEY_RIGHT)) {
			player.applyForce(moveForce, 0);
		}
		if (player.onGround()) {
			if ((input.isKeyPressed(Input.KEY_LCONTROL))
					|| (input.isKeyPressed(Input.KEY_RCONTROL))) {
				if (player.facingRight()) {
					player.applyForce(0, -jumpForce);
				} else {
					player.applyForce(0, -jumpForce);
				}
			}
		}
		if (!input.isKeyDown(Input.KEY_SPACE)) {
			if (player.jumping()) {
				player.setVelocity(player.getVelX(), player.getVelY() * 0.95f);
			}
		}

		if (input.isKeyPressed(Input.KEY_ESCAPE)) {
			container.exit();
		}

		totalDelta += delta;

		// setup the player's moving flag, this control the animation
		player.setMoving(false);
		if (input.isKeyDown(Input.KEY_LEFT)) {
			player.setMoving(true);
		}
		if (input.isKeyDown(Input.KEY_RIGHT)) {
			player.setMoving(true);
		}

		// only check controls at set interval. If we don't do this different
		// frame rates will effect how the controls are interpreted
		if (totalDelta > controlInterval) {
			controlInterval -= totalDelta;

			if (input.isKeyDown(Input.KEY_LEFT)) {
				player.applyForce(-moveForce, 0);
			}
			if (input.isKeyDown(Input.KEY_RIGHT)) {
				player.applyForce(moveForce, 0);
			}
			if (player.onGround()) {
				if ((input.isKeyPressed(Input.KEY_LCONTROL))
						|| (input.isKeyPressed(Input.KEY_RCONTROL))) {
					if (player.facingRight()) {
						player.applyForce(0, -jumpForce);
					} else {
						player.applyForce(0, -jumpForce);
					}
				}
			}
			if (!input.isKeyDown(Input.KEY_LCONTROL)) {
				if (player.jumping()) {
					player.setVelocity(player.getVelX(),
						player.getVelY() * 0.95f);
				}
			}
		}

		// update the environment and hence the physics world
		env.update(delta);

		// calculate screen position clamping to the bounds of the level
		xoffset = player.getX() - width / 2; // TODO what is this?
		yoffset = player.getY() - height / 2;

		Rectangle bounds = env.getBounds();
		if (xoffset < bounds.getX()) {
			xoffset = bounds.getX();
		}
		if (yoffset < bounds.getY()) {
			yoffset = bounds.getY();
		}

		if (xoffset > (bounds.getX() + bounds.getWidth()) - width) {
			xoffset = (bounds.getX() + bounds.getWidth()) - width;
		}
		if (yoffset > (bounds.getY() + bounds.getHeight()) - height) {
			yoffset = (bounds.getY() + bounds.getHeight()) - height;
		}
	}
}