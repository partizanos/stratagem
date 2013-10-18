/*
Stratagem is a model checker for transition systems described using rewriting
rules and strategies.
Copyright (C) 2013 - SMV@Geneva University.
Program written by Edmundo Lopez Bobeda <edmundo [at] lopezbobeda.net>.
This program is free software; you can redistribute it and/or modify
it under the  terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package ch.unige.cui.smv.stratagem.sigmadd

import scala.collection.mutable.HashMap

import ch.unige.cui.smv.stratagem.util.OperationCache

/**
 * Unifies the InductiveIPFImpl cache.
 * @author mundacho
 *
 */
trait IPFImplOperationCache extends SigmaDDIPFFactoryImpl.IPFImpl {

  lazy val unionOperationCache = new HashMap[IPFImplWrapper, SigmaDDIPFFactoryImpl.IPFImpl]
  lazy val interOperationCache = new HashMap[IPFImplWrapper, SigmaDDIPFFactoryImpl.IPFImpl]
  lazy val differenceOperationCache = new HashMap[IPFImplWrapper, SigmaDDIPFFactoryImpl.IPFImpl]

  case class IPFImplWrapper(val ipf: SigmaDDIPFFactoryImpl.IPFImpl) {
    override lazy val hashCode = ipf.hashCode
    override def equals(o: Any): Boolean = o match {
      case IPFImplWrapper(w) => w eq this.ipf
      case _ => false
    }
  }

  /**
   * We override the standard operation to perform a join with cache.
   * @param that the lattice element to join with.
   * @return the union of the this and that.
   */
  abstract override def v(that: LatticeElementType): LatticeElementType = {
    if (this.hashCode < that.hashCode) (that v this) else unionOperationCache.getOrElseUpdate(IPFImplWrapper(that), super.v(that))
  }

  /**
   * We override the standard operation to perform a meet with cache.
   * @param that the lattice element to intersect with.
   * @return the intersection of this and that.
   */
  abstract override def ^(that: LatticeElementType): LatticeElementType = {
    // order the parameters
    if (this.hashCode < that.hashCode) (that ^ this) else interOperationCache.getOrElseUpdate(IPFImplWrapper(that), super.^(that))
  }

  /**
   * We override the standard operation to perform a difference with cache.
   * @param that is the lattice element to subtract.
   * @return the difference of this minus that.
   */
  abstract override def \(that: LatticeElementType): LatticeElementType = {
    differenceOperationCache.getOrElseUpdate(IPFImplWrapper(that), super.\(that))
  }
}