# ⚡ AllayCommand

> Uma framework leve, extensível e baseada em annotations para criação de comandos em Bukkit/Spigot.

O **AllayCommand** foi desenvolvido para simplificar a criação e o gerenciamento de comandos em plugins Bukkit/Spigot.

Com uma API baseada em **annotations**, **Argument Resolvers** e **Reflection**, você pode criar comandos completos sem precisar lidar diretamente com o sistema tradicional de registro de comandos do Bukkit.

---

## ✨ Recursos

* 🏷️ Comandos baseados em **annotations**
* 🔀 Suporte a **aliases**
* 🔐 Sistema de **permissões**
* 🖥️ Controle de execução pelo **Console**
* 💬 Argumentos com `@Remaining`
* 🧩 Argumentos opcionais com `@Optional`
* 🎯 Valores padrão com `@Default`
* 🔄 **Argument Resolvers personalizados**
* 🧠 Conversão automática de argumentos
* 📦 Registro dinâmico de comandos
* ⚡ API simples e extensível
* 🔌 Suporte a tipos personalizados através de resolvers

---

## 📦 Instalação

Adicione o **AllayCommand** ao seu projeto e inicialize o `CommandManager` no seu plugin:

```java
CommandManager commandManager = new CommandManager(this);
```

Depois, registre suas classes de comandos:

```java
commandManager.registerCommands(
        new ExampleCommand()
);
```

---

# 🚀 Criando seu primeiro comando

Crie uma classe e utilize `@CommandContext`:

```java
public class ExampleCommand {

    @CommandContext(
            name = "hello",
            description = "Envia uma mensagem de teste"
    )
    public void hello(CommandSender sender) {
        sender.sendMessage("§aOlá, mundo!");
    }
}
```

Depois registre a classe:

```java
commandManager.registerCommands(
        new ExampleCommand()
);
```

Agora o comando estará disponível:

```text
/hello
```

---

# 🔐 Permissões

Você pode definir uma permissão diretamente no comando:

```java
@CommandContext(
        name = "admin",
        permission = "allay.admin"
)
public void admin(CommandSender sender) {
    sender.sendMessage("§aVocê possui permissão!");
}
```

---

# 🖥️ Comandos para jogadores

Por padrão, os comandos podem ser executados pelo Console.

Caso o comando seja exclusivo para jogadores:

```java
@CommandContext(
        name = "spawn",
        allowedConsole = false
)
public void spawn(Player player) {
    player.sendMessage("§aTeleportando...");
}
```

---

# 🔀 Aliases

Adicione aliases diretamente na annotation:

```java
@CommandContext(
        name = "teleport",
        aliases = {"tp", "tele"}
)
public void teleport(CommandSender sender) {
    sender.sendMessage("§aTeleport!");
}
```

Agora os três comandos funcionarão:

```text
/teleport
/tp
/tele
```

---

# 🧩 Argumentos

O AllayCommand possui suporte a **Argument Resolvers**, permitindo que os argumentos sejam convertidos automaticamente.

Por exemplo:

```java
@CommandContext(name = "heal")
public void heal(Player player, Player target) {
    target.setHealth(target.getMaxHealth());

    player.sendMessage("§aJogador curado!");
}
```

Ao executar:

```text
/heal Notch
```

O `PlayerResolver` será responsável por encontrar o jogador e entregar o objeto `Player` ao método.

---

# 🎯 Valores padrão

Utilize `@Default` para definir um valor caso o argumento não seja informado:

```java
@CommandContext(name = "give")
public void give(
        Player player,
        @Default("1") int amount
) {
    player.sendMessage("§aQuantidade: " + amount);
}
```

Assim, ambos funcionam:

```text
/give 10
/give
```

Quando nenhum valor for informado:

```text
amount = 1
```

---

# 🟢 Argumentos opcionais

Utilize `@Optional` quando um argumento puder não ser informado:

```java
@CommandContext(name = "message")
public void message(
        Player player,
        @Optional String message
) {
    if (message == null) {
        player.sendMessage("§7Nenhuma mensagem informada.");
        return;
    }

    player.sendMessage("§f" + message);
}
```

Também é possível definir um valor padrão:

```java
@Optional("Nenhuma mensagem")
String message
```

---

# 📝 Argumentos restantes

Para capturar todos os argumentos restantes como uma única `String`, utilize `@Remaining`:

```java
@CommandContext(name = "say")
public void say(
        Player player,
        @Remaining String message
) {
    player.sendMessage("§f" + message);
}
```

Executando:

```text
/say Olá mundo, tudo bem?
```

O parâmetro receberá:

```text
Olá mundo, tudo bem?
```

Isso é especialmente útil para comandos como:

```text
/ban <jogador> <motivo>
/broadcast <mensagem>
/tell <jogador> <mensagem>
```

---

# 🧠 Argument Resolvers

O sistema de **Argument Resolvers** é uma das principais funcionalidades do AllayCommand.

Cada tipo pode possuir seu próprio resolver:

```java
public interface ArgumentResolver<T> {

    Class<T> type();

    ResolveResult<T> resolve(
            CommandSender sender,
            Parameter parameter,
            String arg
    );
}
```

Isso permite que a framework seja facilmente expandida sem precisar modificar o `CommandManager`.

---

# 🔧 Criando um Resolver personalizado

Imagine que seu plugin possui uma classe `User`:

```java
public class User {

    private final UUID uuid;

    public User(UUID uuid) {
        this.uuid = uuid;
    }
}
```

Você pode criar um resolver:

```java
public class UserResolver implements ArgumentResolver<User> {

    @Override
    public Class<User> type() {
        return User.class;
    }

    @Override
    public ResolveResult<User> resolve(
            CommandSender sender,
            Parameter parameter,
            String arg
    ) {
        User user = UserCache.findByName(arg).orElse(null);

        if (user == null) {
            return ResolveResult.error("user_not_found");
        }

        return ResolveResult.success(user);
    }
}
```

Registre o resolver:

```java
commandManager.registerResolver(
        new UserResolver()
);
```

Agora você pode utilizar `User` diretamente nos comandos:

```java
@CommandContext(name = "profile")
public void profile(
        Player player,
        User user
) {
    // O User já foi resolvido pelo framework.
}
```

---

# 📚 Resolvers padrão

O AllayCommand já possui resolvers para tipos comuns:

| Tipo          | Resolver           |
| ------------- | ------------------ |
| `String`      | `StringResolver`   |
| `Player`      | `PlayerResolver`   |
| `Integer`     | `IntegerResolver`  |
| `int`         | `IntegerResolver`  |
| `Double`      | `DoubleResolver`   |
| `double`      | `DoubleResolver`   |
| `Long`        | `LongResolver`     |
| `long`        | `LongResolver`     |
| `GameMode`    | `GameModeResolver` |
| `Enchantment` | `EnchantResolver`  |

---

# 🏗️ Estrutura

A framework é dividida em componentes com responsabilidades específicas:

```text
AllayCommand
│
├── CommandManager
│   └── Gerenciamento e execução dos comandos
│
├── DynamicCommand
│   └── Integração com o sistema de comandos do Bukkit
│
├── CommandMethod
│   └── Representação dos métodos registrados
│
├── ResolverRegistry
│   └── Registro dos Argument Resolvers
│
├── ArgumentResolver
│   └── Conversão dos argumentos
│
└── ResolveResult
    └── Resultado da resolução dos argumentos
```

---

# 🎯 Objetivo

O objetivo do **AllayCommand** é tornar a criação de comandos mais simples, organizada e extensível, permitindo que o desenvolvedor se concentre na lógica do plugin em vez da implementação do sistema de comandos.

Em vez de lidar com:

```java
String[] args
```

e fazer manualmente:

```java
Integer.parseInt(...)
Bukkit.getPlayer(...)
```

você pode trabalhar diretamente com os tipos que precisa:

```java
public void command(
        Player player,
        User user,
        int amount
) {
    // lógica do comando
}
```

---

## 📄 Licença

Este projeto ainda não possui uma licença definida.

---

## 👨‍💻 Autor

Desenvolvido por **Stompado**.

**AllayCommand** — tornando comandos Bukkit mais simples. ⚡
