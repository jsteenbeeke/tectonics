package com.jeroensteenbeeke.worldgen.tectonics;

import java.util.Random;

class Plate {

	private final WorldDimension dimension;
	private final Random randSource;
	private final HeightMap map;
	private final AgeMap age_map;
	private final Bounds bounds;
	private final Mass mass;
	private final Movement movement;
	ISegments segments;
	ISegmentCreator segmentCreator;
	
	public Plate(long seed, float[] m, long w, long h, long x, long y,
            long plate_age, WorldDimension worldDimension) {
		Dimension dim =  new Dimension(w, h);
		
		this.randSource = new Random(seed);
		this.mass = new Mass.Builder(m, dim).build();
this.map = new HeightMap(m, w, h);
	    this.age_map = new AgeMap(w, h);
	    this.dimension = worldDimension;
	    this.movement = new Movement(randSource, worldDimension);
		
		final long plate_area = w * h;
		this.bounds = new Bounds(worldDimension, new FloatPoint(x, y), dim);
		
		
	}
	
	    /// Increment collision counter of the continent at given location.
	    ///
	    /// @param  wx  X coordinate of collision point on world map.
	    /// @param  wy  Y coordinate of collision point on world map.
	    /// @return Surface area of the collided continent (HACK!)
	    public long addCollision(long wx, long wy) {
	    	// TODO
	    }

	    /// Add crust to plate as result of continental collision.
	    ///
	    /// @param  x   Location of new crust on global world map (X).
	    /// @param  y   Location of new crust on global world map (Y).
	    /// @param  z   Amount of crust to add.
	    /// @param  t   Time of creation of new crust.
	    /// @param activeContinent Segment ID of the continent that's processed.
	    void addCrustByCollision(long x, long y, float z, long t, long activeContinent) {
	    	// TODO
		}

	    /// Simulates subduction of oceanic plate under this plate.
	    ///
	    /// Subduction is simulated by calculating the distance on surface
	    /// that subducting sediment will travel under the plate until the
	    /// subducting slab has reached certain depth where the heat triggers
	    /// the melting and uprising of molten magma. 
	    ///
	    /// @param  x   Origin of subduction on global world map (X).
	    /// @param  y   Origin of subduction on global world map (Y).
	    /// @param  z   Amount of sediment that subducts.
	    /// @param  t   Time of creation of new crust.
	    /// @param  dx  Direction of the subducting plate (X).
	    /// @param  dy  Direction of the subducting plate (Y).
	    void addCrustBySubduction(long x, long y, float z, long t,
	        float dx, float dy) {
	    	// TODO
		    }

	    /// Add continental crust from this plate as part of other plate.
	    ///
	    /// Aggregation of two continents is the event where the collided
	    /// pieces of crust fuse together at the point of collision. It is
	    /// crucial to merge not only the collided pieces of crust but also
	    /// the entire continent that's part of the colliding tad of crust
	    /// However, because one plate can contain many islands and pieces of
	    /// continents, the merging must be done WITHOUT merging the entire
	    /// plate and all those continental pieces that have NOTHING to do with
	    /// the collision in question.
	    ///
	    /// @param  p   Pointer to the receiving plate.
	    /// @param  wx  X coordinate of collision point on world map.
	    /// @param  wy  Y coordinate of collision point on world map.
	    /// @return Amount of crust aggregated to destination plate.
	    float aggregateCrust(Plate p, long wx, long wy) {
	    	// TODO
		    }

	    /// Decrease the speed of plate amount relative to its total mass.
	    ///
	    /// Method decreses the speed of plate due to friction that occurs when
	    /// two plates collide. The amount of reduction depends of the amount
	    /// of mass that causes friction (i.e. that has collided) compared to
	    /// the total mass of the plate. Thus big chunk of crust colliding to
	    /// a small plate will halt it but have little effect on a huge plate.
	    ///
	    /// @param  deforming_mass Amount of mass deformed in collision.
	    void applyFriction(float deforming_mass) {
	    	// TODO
		    }

	    /// Method collides two plates according to Newton's laws of motion.
	    ///
	    /// The velocity and direction of both plates are updated using
	    /// impulse forces following the collision according to Newton's laws
	    /// of motion. Deformations are not applied but energy consumed by the
	    /// deformation process is taken away from plate's momentum.
	    ///
	    /// @param  p   Plate to test against.
	    /// @param  wx  X coordinate of collision point on world map.
	    /// @param  wy  Y coordinate of collision point on world map.
	    /// @param  coll_mass Amount of colliding mass from source plate.
	    void collide(Plate p, long xw, long wy, float coll_mass) {
	    	// TODO
		    }

	    /// Apply plate wide erosion algorithm.
	    ///
	    /// Plates total mass and the center of mass are updated.
	    ///
	    /// @param  lower_bound Sets limit below which there's no erosion.
	    void erode(float lower_bound) {}

	    /// Retrieve collision statistics of continent at given location.
	    ///
	    /// @param  wx  X coordinate of collision point on world map.
	    /// @param  wy  Y coordinate of collision point on world map.
	    /// @param[in, out] count Destination for the count of collisions.
	    /// @param[in, out] count Destination for the % of area that collided.
	    void getCollisionInfo(long wx, long wy, long count,
	                            float ratio) {
	    	// TODO
		    }

	    /// Retrieve the surface area of continent lying at desired location.
	    ///
	    /// @param  wx  X coordinate of collision point on world map.
	    /// @param  wy  Y coordinate of collision point on world map.
	    /// @return Area of continent at desired location or 0 if none.
	    long getContinentArea(long wx, long wy) {
	    	// TODO
		    }

	    /// Get the amount of plate's crustal material at some location.
	    ///
	    /// @param  x   Offset on the global world map along X axis.
	    /// @param  y   Offset on the global world map along Y axis.
	    /// @return     Amount of crust at requested location.
	    float getCrust(long x, long y) {
	    	// TODO
		    }

	    /// Get the timestamp of plate's crustal material at some location.
	    ///
	    /// @param  x   Offset on the global world map along X axis.
	    /// @param  y   Offset on the global world map along Y axis.
	    /// @return     Timestamp of creation of crust at the location.
	    ///                     Zero is returned if location contains no crust.
	    long getCrustTimestamp(long x, long y) {
	    	// TODO
		    }

	    /// Get pointers to plate's data.
	    ///
	    /// @param  c   Adress of crust height map is stored here.
	    /// @param  t   Adress of crust timestamp map is stored here.
	    void getMap(final HeightMap heights, final HeightMap timestamps) {
// TODO Check parameter types
	    	// TODO implement
	    
	    }

	    void move() {
	    	// TODO
		    } ///< Moves plate along it's trajectory.

	    /// Clear any earlier continental crust partitions.
	    ///
	    /// Plate has an internal bookkeeping of distinct areas of continental
	    /// crust for more realistic collision responce. However as the number
	    /// of collisions that plate experiences grows, so does the bookkeeping
	    /// of a continent become more and more inaccurate. Finally it results
	    /// in striking artefacts that cannot overlooked.
	    ///
	    /// To alleviate this problem without the need of per iteration
	    /// recalculations plate supplies caller a method to reset its
	    /// bookkeeping and start clean.
	    void resetSegments() {
	    	// TODO
		    }

	    /// Remember the currently processed continent's segment number.
	    ///
	    /// @param  coll_x  Origin of collision on global world map (X).
	    /// @param  coll_y  Origin of collision on global world map (Y).
	    /// @return the Id of the continent being processed
	    long selectCollisionSegment(long coll_x, long coll_y) {
	    	// TODO
		    }

	    /// Set the amount of plate's crustal material at some location.
	    ///
	    /// If amount of crust to be set is negative, it'll be set to zero.
	    ///
	    /// @param  x   Offset on the global world map along X axis.
	    /// @param  y   Offset on the global world map along Y axis.
	    /// @param  z   Amount of crust at given location.
	    /// @param  t   Time of creation of new crust.
	    void setCrust(long x, long y, float z, long t) {
	    	// TODO
		    }

	    float getMass() { return mass.getMass(); }
	    float getMomentum() { return movement.momentum(mass); }) {
	    	// TODO
}    
	    long getHeight() { return bounds.height(); }
	    long  getLeftAsUint() { return bounds.leftAsUint(); }
	    long  getTopAsUint() { return bounds.topAsUint(); }
	    float getVelocity() { return movement.getVelocity(); }

	    Platec::FloatVector velocityUnitVector() const {
	        return movement.velocityUnitVector() {}
	    }

	    /// @Deprecated, use velocityUnitVector instead
	    float getVelX() { return movement.velX(); }
	    /// @Deprecated, use velocityUnitVector instead
	    float getVelY() { return movement.velY(); }
	    
	    long getWidth() { return bounds.width(); }
	    boolean   isEmpty() { return mass.null(); }
	    float getCx() { return mass.getCx(); }
	    float getCy() { return mass.getCy(); }
	    FloatPoint massCenter() const {
	        return mass.massCenter();
	    }

	    void decImpulse(const Platec::FloatVector& delta) {
	       movement.decDx(delta.x());
	       movement.decDy(delta.y());
	    }

	    // @Deprecated, use decImpulse instead
	    void decDx(float delta) { movement.decDx(delta); }
	    void decDy(float delta) { movement.decDy(delta); }

	    // visible for testing
	    void calculateCrust(long x, long y, long index, 
	            float& w_crust, float& e_crust, float& n_crust, float& s_crust,
	            long& w, long& e, long& n, long& s) {}

	    // Visible for testing
	    void injectSegments(ISegments* segments)
	    {
	        delete segments;
	        segments = segments;
	    }

	    // Visible for testing
	    void injectBounds(IBounds* bounds)
	    {
	        delete bounds;
	        bounds = bounds;
	    }
}
