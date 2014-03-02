package icbm.sentry.turret.ai;

import icbm.sentry.interfaces.IAutoSentry;
import icbm.sentry.interfaces.ISentry;
import icbm.sentry.interfaces.ISentryContainer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import universalelectricity.api.vector.Vector3;
import universalelectricity.api.vector.VectorWorld;

/**
 * AI for the sentry objects
 * 
 * @author DarkGuardsman
 */
public class SentryAI
{
	private ISentryContainer container;
	private IEntitySelector entitySelector;
	private int rotationDelayTimer = 0;
	private int targetLostTimer = 0;
	private LookHelper lookHelper;

	public static final boolean debugMode = false;

	public static void debug(String str)
	{
		if (debugMode)
			System.out.println("[Sentry AI] " + str);
	}

	public SentryAI(ISentryContainer container, LookHelper lookHelper)
	{
		this.container = container;
		this.lookHelper = lookHelper;
		// TODO get selector from sentry at a later date
		this.entitySelector = new EntitySelectorSentry(this.container);
	}

	public IAutoSentry sentry()
	{
		if (container != null && container.getSentry() != null && container.getSentry() instanceof IAutoSentry)
		{
			return (IAutoSentry) container.getSentry();
		}
		return null;
	}

	public void update()
	{
		if (sentry() != null)
		{
			// Used to debug and force the sentry to look at player to make correct model rotation
			// adjustments.
			/*
			 * List<EntityLivingBase> list =
			 * container.world().selectEntitiesWithinAABB(EntityLivingBase.class,
			 * AxisAlignedBB.getBoundingBox(lookHelper.getCenter().x, lookHelper.getCenter().y,
			 * lookHelper.getCenter().z, lookHelper.getCenter().x, lookHelper.getCenter().y,
			 * lookHelper.getCenter().z).expand(10, 10, 10), null);
			 * Collections.sort(list, new ComparatorClosestEntity(lookHelper.getCenter()));
			 * for (EntityLivingBase entity : list)
			 * {
			 * if (entity instanceof EntityPlayer)
			 * {
			 * lookHelper.lookAtEntity(entity);
			 * return;
			 * }
			 * }
			 */

			debug(" \nUpdate tick");
			// Only get new target if the current is missing or it will switch targets each update
			if (sentry().getTarget() == null)
			{
				debug("\tSearching for target");
				sentry().setTarget(findTarget(container.getSentry(), this.entitySelector, this.container.getSentry().getRange()));
			}

			// If we have a target start aiming logic
			if (sentry().getTarget() != null && isValidTarget(sentry().getTarget(), false))
			{
				if (lookHelper.canEntityBeSeen(sentry().getTarget()))
				{
					debug("\tTarget can be seen");
					if (lookHelper.isLookingAt(sentry().getTarget(), 3))
					{
						debug("\tTarget locked and firing weapon");
						this.container.getSentry().fire(sentry().getTarget());
					}
					else
					{
						debug("\tPowering servos to aim at target");
						lookHelper.lookAtEntity(sentry().getTarget());
					}
					targetLostTimer = 0;
				}
				else
				{
					debug("\tSight on target lost");
					// Drop the target after 2 seconds of no sight
					if (targetLostTimer >= 100)
					{
						sentry().setTarget(null);
					}
					targetLostTimer++;
				}
			}
			else
			{
				debug("\tNo Target Selected. Wandering.");
				// Only start random rotation after a second of no target
				if (targetLostTimer >= 20)
				{
					if (rotationDelayTimer >= 100)
					{
						rotationDelayTimer = 0;
						Vector3 location = new Vector3(this.container.x(), this.container.y(), this.container.z());
						location.add(new Vector3(this.container.world().rand.nextInt(40) - 20, 0, this.container.world().rand.nextInt(40) - 20));
						lookHelper.lookAt(location);
					}

					sentry().setTarget(null);
					rotationDelayTimer++;
				}

				targetLostTimer++;
			}
		}

	}

	protected EntityLivingBase findTarget(ISentry sentry, IEntitySelector targetSelector, int range)
	{
		debug("\t\tTarget selector update");
		List<EntityLivingBase> list = container.world().selectEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(lookHelper.getCenter().x, lookHelper.getCenter().y, lookHelper.getCenter().z, lookHelper.getCenter().x, lookHelper.getCenter().y, lookHelper.getCenter().z).expand(range, range, range), targetSelector);
		Collections.sort(list, new ComparatorOptimalTarget(lookHelper.getCenter()));

		debug("\t\t" + list.size() + " possible targets within "+range);

		for (EntityLivingBase entity : list)
		{
			if (isValidTarget(entity, false))
			{
				return entity;
			}
		}

		return null;
	}

	public boolean isValidTarget(Entity entity, boolean skipSight)
	{
		if (this.entitySelector.isEntityApplicable(entity))
		{
			if (!skipSight)
			{
				boolean flag_bounds = lookHelper.isTargetInBounds(entity);

				if (flag_bounds)
				{
					boolean flag_sight = lookHelper.canEntityBeSeen(entity);
					debug("\t\tisValidTarget: Within bounds?" + flag_bounds + " Can be seen? " + flag_sight);
					return flag_sight;
				}
			}
			else
			{
				return true;
			}
		}

		return false;
	}

	// TODO: add options to this for reversing the targeting filter
	public static class ComparatorOptimalTarget implements Comparator<EntityLivingBase>
	{
		private final VectorWorld location;

		public ComparatorOptimalTarget(VectorWorld location)
		{
			this.location = location;
		}

		public int compare(EntityLivingBase entityA, EntityLivingBase entityB)
		{
			double distanceA = this.location.distance(entityA);
			double distanceB = this.location.distance(entityB);

			if (Math.abs(distanceA - distanceB) < 1.5)
			{
				float healthA = entityA.getHealth();
				float healthB = entityB.getHealth();
				return healthA < healthB ? -1 : (healthA > healthB ? 1 : 0);
			}

			return distanceA < distanceB ? -1 : (distanceA > distanceB ? 1 : 0);
		}
	}

}
