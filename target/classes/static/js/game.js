const API = "";

let selectedClass = "Warrior";
let enemyMaxHp = 100;
let isAnimating = false;

const $ = (id) => document.getElementById(id);

const ENEMY_VISUAL = {
    goblin: { label: "Savage Goblin", class: "goblin" },
    mage: { label: "Dark Sorcerer", class: "mage-enemy" },
    wolf: { label: "Beast", class: "wolf" },
    default: { label: "Hostile Fighter", class: "" },
};

function enemyVisual(name) {
    const n = (name || "").toLowerCase();
    if (n.includes("goblin")) return ENEMY_VISUAL.goblin;
    if (n.includes("mage")) return ENEMY_VISUAL.mage;
    if (n.includes("wolf")) return ENEMY_VISUAL.wolf;
    return ENEMY_VISUAL.default;
}

function weaponClass(cls) {
    const c = (cls || "").toLowerCase();
    if (c === "mage") return "mage-weapon";
    if (c === "archer") return "archer-weapon";
    return "warrior-weapon";
}

async function api(path, options = {}) {
    const res = await fetch(API + path, {
        headers: { "Content-Type": "application/json", Accept: "application/json" },
        ...options,
    });
    const text = await res.text();
    let data = null;
    try {
        data = text ? JSON.parse(text) : null;
    } catch {
        data = null;
    }
    if (!res.ok) {
        throw new Error(data?.error || data?.message || `Request failed (${res.status})`);
    }
    return data;
}

function showToast(message, isError = false) {
    const toast = $("toast");
    toast.textContent = message;
    toast.classList.remove("hidden", "error");
    if (isError) toast.classList.add("error");
    clearTimeout(showToast._t);
    showToast._t = setTimeout(() => toast.classList.add("hidden"), 3500);
}

function setServerOnline(online) {
    $("serverStatus").classList.toggle("online", online);
    $("serverStatus").classList.toggle("offline", !online);
    $("serverStatus").querySelector("span:last-child").textContent = online
        ? "Arena server live"
        : "Server offline — run RUN-SERVER.bat";
}

async function checkServer() {
    try {
        await api("/api/health");
        setServerOnline(true);
    } catch {
        setServerOnline(false);
    }
}

function pct(current, max) {
    if (!max || max <= 0) return 0;
    return Math.max(0, Math.min(100, (current / max) * 100));
}

function setArenaAnnounce(text, dramatic = false) {
    const el = $("arenaAnnounce");
    el.textContent = text;
    el.classList.toggle("dramatic", dramatic);
}

function updatePlayer(player) {
    if (!player) return;
    $("playerNameDisplay").textContent = player.name;
    $("playerLevel").textContent = player.level;
    $("playerXp").textContent = player.experience;
    $("playerAtk").textContent = player.attackPower;
    const max = player.maxHealth ?? player.health;
    $("playerHpText").textContent = `${player.health} / ${max} HP`;
    $("playerHpBar").style.width = pct(player.health, max) + "%";
}

function updateEnemy(enemy) {
    if (!enemy) return;
    const vis = enemyVisual(enemy.name);
    $("enemyName").textContent = enemy.name;
    $("enemyTypeLabel").textContent = vis.label;
    $("enemyAtk").textContent = enemy.attackPower;

    const body = $("enemyBody");
    body.className = "sprite-body enemy-body " + vis.class;

    if (enemy.health > enemyMaxHp) enemyMaxHp = enemy.health;
    if (enemy.health >= enemyMaxHp * 0.85) enemyMaxHp = enemy.health;

    $("enemyHpText").textContent = `${enemy.health} / ${enemyMaxHp} HP`;
    $("enemyHpBar").style.width = pct(enemy.health, enemyMaxHp) + "%";
}

function renderInventory(items) {
    const list = $("inventoryList");
    const empty = $("inventoryEmpty");
    list.innerHTML = "";
    if (!items || items.length === 0) {
        empty.classList.remove("hidden");
        return;
    }
    empty.classList.add("hidden");
    items.forEach((item) => {
        const li = document.createElement("li");
        li.innerHTML = `<div class="item-info"><strong>${escapeHtml(item.name)}</strong><small>${item.type}</small></div>`;
        const btn = document.createElement("button");
        btn.className = "btn btn-small";
        btn.textContent = "Use";
        btn.onclick = () => useItem(item.name);
        li.appendChild(btn);
        list.appendChild(li);
    });
}

function escapeHtml(s) {
    const d = document.createElement("div");
    d.textContent = s;
    return d.innerHTML;
}

function classifyLogLine(line) {
    if (line.startsWith("—")) return "round";
    if (line.includes("CRITICAL") || line.includes("CRITICAL STRIKE")) return "critical";
    if (line.includes("DEFEATED") || line.includes("DEFEAT") || line.includes("collapses")) return "defeat";
    if (line.includes("crowd") || line.includes("erupts") || line.includes("gains") && line.includes("XP")) return "victory";
    return "";
}

function addLogLine(line, extraClass = "") {
    const log = $("battleLog");
    const li = document.createElement("li");
    const auto = classifyLogLine(line);
    li.className = [auto, extraClass, "latest"].filter(Boolean).join(" ");
    li.textContent = line;
    log.querySelectorAll(".latest").forEach((el) => el.classList.remove("latest"));
    log.prepend(li);
    while (log.children.length > 14) log.removeChild(log.lastChild);
}

function clearLog() {
    $("battleLog").innerHTML = "";
}

function showFloatingDamage(text, type, critical = false) {
    const layer = $("damageLayer");
    const el = document.createElement("span");
    el.className = "floating-dmg " + type + (critical ? " critical" : "");
    el.textContent = "-" + text;
    layer.appendChild(el);
    setTimeout(() => el.remove(), 1200);
}

function shakeArena() {
    $("combatStage").classList.add("shake");
    setTimeout(() => $("combatStage").classList.remove("shake"), 450);
}

function sleep(ms) {
    return new Promise((r) => setTimeout(r, ms));
}

async function playBattleAnimation(battle, playerName) {
    if (!battle || !battle.lines || battle.lines.length === 0) return;

    const playerEl = $("playerCombatant");
    const enemyEl = $("enemyCombatant");
    const btn = $("btnAttack");
    isAnimating = true;
    btn.disabled = true;

    for (const line of battle.lines) {
        addLogLine(line);
        setArenaAnnounce(line.length > 80 ? line.slice(0, 77) + "…" : line, line.includes("CRITICAL") || line.includes("DEFEAT"));

        const lower = line.toLowerCase();
        const isPlayerStrike = lower.includes(playerName?.toLowerCase()) &&
            (lower.includes("blade") || lower.includes("arrow") || lower.includes("arcane") || lower.includes("strikes") || lower.includes("channels") || lower.includes("looses") || lower.includes("rushes"));

        if (isPlayerStrike || lower.includes("critical strike")) {
            playerEl.classList.add("attacking");
            await sleep(280);
            enemyEl.classList.add("hit");
            if (battle.playerDamageDealt > 0) {
                showFloatingDamage(battle.playerDamageDealt, "enemy-hit", battle.criticalHit);
                shakeArena();
            }
            await sleep(320);
            playerEl.classList.remove("attacking");
            enemyEl.classList.remove("hit");
        } else if (battle.playerDodged && lower.includes("rolls aside")) {
            playerEl.classList.add("dodge");
            await sleep(500);
            playerEl.classList.remove("dodge");
        } else if (lower.includes("takes") || lower.includes("suffers") || lower.includes("fangs") || lower.includes("curse")) {
            enemyEl.classList.add("attacking");
            await sleep(280);
            playerEl.classList.add("hit");
            if (battle.enemyDamageDealt > 0) {
                showFloatingDamage(battle.enemyDamageDealt, "player-hit", false);
            }
            await sleep(320);
            enemyEl.classList.remove("attacking");
            playerEl.classList.remove("hit");
        } else if (battle.enemyDefeated && lower.includes("defeated")) {
            enemyEl.classList.add("hit");
            shakeArena();
            await sleep(400);
            enemyEl.classList.remove("hit");
        }

        await sleep(450);
    }

    isAnimating = false;
    btn.disabled = false;
}

function applyGameState(data, skipAnimation = false) {
    $("waveNum").textContent = data.wave ?? 1;
    const status = data.statusEffect || "READY";
    $("statusEffect").textContent = "Condition: " + status;

    updatePlayer(data.player);
    updateEnemy(data.enemy);
    renderInventory(data.inventory);

    const dead = data.player && data.player.health <= 0;
    if (dead) {
        $("btnAttack").disabled = true;
        setArenaAnnounce("You have fallen in battle…", true);
        showToast("Defeated! Leave the arena and try again.", true);
    }

    if (data.battle && !skipAnimation) {
        playBattleAnimation(data.battle, data.player?.name);
    } else if (data.battle?.lines) {
        data.battle.lines.forEach((l) => addLogLine(l));
        if (data.message) setArenaAnnounce(data.battle.lines[data.battle.lines.length - 1] || data.message);
    } else if (data.message) {
        addLogLine(data.message);
        setArenaAnnounce(data.message);
    }
}

async function startGame(name, characterClass) {
    selectedClass = characterClass;
    $("playerClassDisplay").textContent = characterClass;
    const weapon = $("playerWeapon");
    weapon.className = "sprite-weapon " + weaponClass(characterClass);

    const data = await api("/api/start-game", {
        method: "POST",
        body: JSON.stringify({ name, characterClass }),
    });

    enemyMaxHp = data.enemy?.health ?? 100;
    clearLog();
    $("screenStart").classList.add("hidden");
    $("screenGame").classList.remove("hidden");
    $("damageLayer").innerHTML = "";

    updatePlayer(data.player);
    updateEnemy(data.enemy);
    renderInventory(data.inventory);

    if (data.battle?.lines) {
        for (const line of data.battle.lines) {
            addLogLine(line);
            await sleep(600);
        }
    }
    setArenaAnnounce(name + " enters the arena!", true);
    await loadLeaderboard();
    showToast("Fight! Press STRIKE to attack.");
}

async function attack() {
    if (isAnimating) return;
    $("btnAttack").disabled = true;
    try {
        const data = await api("/api/attack", { method: "POST" });
        if (data.enemy?.health > enemyMaxHp) enemyMaxHp = data.enemy.health;
        updatePlayer(data.player);
        updateEnemy(data.enemy);
        renderInventory(data.inventory);
        $("waveNum").textContent = data.wave ?? 1;
        $("statusEffect").textContent = "Condition: " + (data.statusEffect || "—");

        if (data.battle) {
            await playBattleAnimation(data.battle, data.player?.name);
        }

        const dead = data.player && data.player.health <= 0;
        $("btnAttack").disabled = dead || isAnimating;
        if (data.battle?.enemyDefeated) await loadLeaderboard();
    } finally {
        if (!$("btnAttack").disabled && !isAnimating) $("btnAttack").disabled = false;
    }
}

async function useItem(itemName) {
    const data = await api("/api/inventory/use", {
        method: "POST",
        body: JSON.stringify({ itemName }),
    });
    applyGameState(data, true);
    if (data.battle?.lines) {
        for (const line of data.battle.lines) addLogLine(line);
    }
    showToast("Used " + itemName);
}

async function loadLeaderboard() {
    try {
        const entries = await api("/api/leaderboard");
        const list = $("leaderboardList");
        const empty = $("leaderboardEmpty");
        list.innerHTML = "";
        if (!entries?.length) {
            empty.classList.remove("hidden");
            return;
        }
        empty.classList.add("hidden");
        entries.forEach((e, i) => {
            const li = document.createElement("li");
            li.textContent = `${i + 1}. ${e.playerName} — Lv.${e.level} (${e.score} pts)`;
            list.appendChild(li);
        });
    } catch { /* ignore */ }
}

function newGame() {
    $("screenGame").classList.add("hidden");
    $("screenStart").classList.remove("hidden");
    $("btnAttack").disabled = false;
    isAnimating = false;
}

$("startForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const name = $("playerName").value.trim() || "Hero";
    const characterClass = document.querySelector('input[name="characterClass"]:checked')?.value || "Warrior";
    try {
        await startGame(name, characterClass);
    } catch (err) {
        showToast(err.message, true);
    }
});

$("btnAttack").addEventListener("click", () => attack().catch((err) => showToast(err.message, true)));
$("btnNewGame").addEventListener("click", newGame);
$("btnRefreshBoard").addEventListener("click", () => loadLeaderboard().catch(() => {}));

checkServer();
setInterval(checkServer, 15000);
