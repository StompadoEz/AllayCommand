package allayplugins.stompado.resolver.enchant;

import allayplugins.stompado.resolver.ArgumentResolver;
import allayplugins.stompado.resolver.result.ResolveResult;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class EnchantResolver implements ArgumentResolver<Enchantment> {

    private static final Map<String, Enchantment> ALIASES = new HashMap<>();

    static {
        for (Enchantment ench : Enchantment.values()) {
            if (ench != null && ench.getName() != null) {
                ALIASES.put(ench.getName().toUpperCase(), ench);
            }
        }

        ALIASES.put("SHARPNESS", Enchantment.DAMAGE_ALL);
        ALIASES.put("DAMAGE", Enchantment.DAMAGE_ALL);
        ALIASES.put("PROTECTION", Enchantment.PROTECTION_ENVIRONMENTAL);
        ALIASES.put("PROT", Enchantment.PROTECTION_ENVIRONMENTAL);
        ALIASES.put("UNBREAKING", Enchantment.DURABILITY);
        ALIASES.put("EFFICIENCY", Enchantment.DIG_SPEED);
        ALIASES.put("FORTUNE", Enchantment.LOOT_BONUS_BLOCKS);
        ALIASES.put("LOOTING", Enchantment.LOOT_BONUS_MOBS);
        ALIASES.put("POWER", Enchantment.ARROW_DAMAGE);
        ALIASES.put("FLAME", Enchantment.ARROW_FIRE);
        ALIASES.put("PUNCH", Enchantment.ARROW_KNOCKBACK);
        ALIASES.put("INFINITY", Enchantment.ARROW_INFINITE);
    }

    @Override
    public Class<Enchantment> type() {
        return Enchantment.class;
    }

    @Override
    public ResolveResult<Enchantment> resolve(CommandSender sender, Parameter parameter, String argument) {
        if (argument == null) {
            return ResolveResult.error("Nenhum encantamento informado.");
        }

        String input = argument.trim().toUpperCase();

        Enchantment enchantment = ALIASES.get(input);
        if (enchantment != null) {
            return ResolveResult.success(enchantment);
        }

        for (Map.Entry<String, Enchantment> entry : ALIASES.entrySet()) {
            if (entry.getKey().startsWith(input)) {
                return ResolveResult.success(entry.getValue());
            }
        }

        return ResolveResult.error(
                "Encantamento inválido! Exemplo: sharpness, prot, unbreaking..."
        );
    }
}
