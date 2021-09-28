package mint.modules.combat

import mint.clickgui.setting.Setting
import mint.events.Render3DEvent
import mint.modules.Module
import mint.utils.BlockUtil
import mint.utils.EntityUtil
import mint.utils.MathUtil
import mint.utils.RenderUtil
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import java.awt.Color


/**
 * @author kambing
 * @since 26/9/21
 */
object KotlinAura : Module("KotlinAura", Category.COMBAT, "ur mom") {
    @JvmField
    var targetRange: Setting<Float> = register(Setting<Any?>("Target Range", 10f, 0f, 15f)) as Setting<Float>
    @JvmField
    var placeRange: Setting<Float> = register(Setting<Any?>("Place Range", 5f, 0f, 6f)) as Setting<Float>
    @JvmField
    var breakRange: Setting<Float> = register(Setting<Any?>("Break Range", 5f, 0f, 6f)) as Setting<Float>
    @JvmField
    var minDamage: Setting<Float> = register(Setting<Any?>("Min Damage", 6f, 0f, 12f)) as Setting<Float>
    @JvmField
    var maxSelfDamage: Setting<Float> = register(Setting<Any?>("Max Self Damage", 8f, 0f, 12f)) as Setting<Float>
    var minHealth: Setting<Float> = register(Setting<Any?>("Min Health", 10f, 0f, 36f)) as Setting<Float>
    var packetBreak: Setting<Boolean> = register(Setting<Any?>("Packet Break", true)) as Setting<Boolean>
    var onlySelfCrystal: Setting<Boolean> = register(Setting<Any?>("Break Self Only", true)) as Setting<Boolean>
    @JvmField
    var renderParent: Setting<Boolean> = register(Setting<Any?>("Render", true, false)) as Setting<Boolean>
    var r: Setting<Float> = register(Setting<Any?>("R", 255, 0, 255)) as Setting<Float>
    var g: Setting<Float> = register(Setting<Any?>("G", 255, 0, 255)) as Setting<Float>
    var b: Setting<Float> = register(Setting<Any?>("B", 255, 0, 255)) as Setting<Float>
    var a: Setting<Float> = register(Setting<Any?>("A", 255, 0, 255)) as Setting<Float>

    override fun onUpdate() {
        target = EntityUtil.getTarget(targetRange.getValue())
        doPlace()
        doBreak()
    }

    var placePos: BlockPos? = null
    var target: EntityPlayer? = null

    fun doPlace() {
        if (target == null) return
        var maxDamage = 0.5f
        val sphere = BlockUtil.getSphere(this.placeRange.getValue().toDouble(), true)
        val size = sphere.size
        var i = 0
        while (i < size) {
            val pos = sphere[i]
            val self = EntityUtil.calculatePos(pos, Module.mc.player)
            if (BlockUtil.canPlaceCrystal(pos, true)) {
                var damage: Float = EntityUtil.calculatePos(pos, target)
                if (EntityUtil.getHealth(Module.mc.player) > self + 0.5f && this.maxSelfDamage.getValue() > self && EntityUtil.calculatePos(
                        pos,
                        target
                    ) > maxDamage && damage > self && !EntityUtil.isPlayerSafe(target)
                ) {
                    if (damage <= this.minDamage.getValue()) {
                        if (damage <= 2.0f) {
                            ++i
                            continue
                        }
                    }
                    maxDamage = damage
                    placePos = pos
                }
            }
            ++i
        }
        if (placePos != null) {
            mc.connection!!.sendPacket(
                CPacketPlayerTryUseItemOnBlock(
                    placePos,
                    EnumFacing.UP,
                    if (mc.player.heldItemOffhand.getItem() === Items.END_CRYSTAL) EnumHand.OFF_HAND else EnumHand.MAIN_HAND,
                    0.5f,
                    0.5f,
                    0.5f
                )
            )
        }
    }

    fun doBreak() {
        for (crystal in Module.mc.world.loadedEntityList) {
            if (crystal is EntityEnderCrystal) {
                if (onlySelfCrystal.getValue() && placePos != null && crystal.posY.toInt() != placePos!!.y + 1) continue
                if (crystal.getDistance(Module.mc.player) > MathUtil.square(breakRange.getValue().toDouble())) continue
                if (packetBreak.getValue()) {
                    Module.mc.connection!!.sendPacket(CPacketUseEntity(crystal))
                } else {
                    Module.mc.playerController.attackEntity(Module.mc.player, crystal)
                }
            }
        }
    }
        override fun onRender3D(event: Render3DEvent?) {
            var color = Color(r.getValue().toInt(), g.getValue().toInt(), b.getValue().toInt(), a.getValue().toInt())
            if (placePos != null) {
                RenderUtil.drawBox(placePos, color)
            }
        }
    }