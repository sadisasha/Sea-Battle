package model;

import static model.CellState.*;
import static model.ShotResult.*;

class Cell
{
	AliveChecker locatedShip;
	CellState state;

	public Cell()
	{
		// default state
		state = EMPTY;
	}

	public ShotResult getShot()
	{
		if (state == EMPTY) {
			state = SHELLED;
			return MISSES;
		}
		if (state == SHIP) {
			state = DAMAGED_SHIP;
			if (locatedShip.isAlive()) {
				return HIT;
			}
		}
		return DESTROY;
	}
}