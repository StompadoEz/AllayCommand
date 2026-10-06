package allayplugins.stompado.resolver.material;

import allayplugins.stompado.resolver.ArgumentResolver;
import allayplugins.stompado.resolver.result.ResolveResult;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;

public class MaterialResolver implements ArgumentResolver<Material> {

    @Override
    public Class<Material> type() {
        return Material.class;
    }

    @Override
    public ResolveResult<Material> resolve(CommandSender sender, Parameter parameter, String argument) {
        if (argument == null) {
            return ResolveResult.error("Nenhum material informado.");
        }

        Material material = Material.matchMaterial(argument);

        if (material == null) {
            return ResolveResult.error("Material inválido: " + argument);
        }

        return ResolveResult.success(material);
    }
}