const universalCrafter = extend(Block, "crusher", {
    // Список рецептов: предмет-вход, кол-во, предмет-выход, время
    recipes: [],
    
    load() {
        this.super$load();
    },

    init() {
        this.super$init();
        // ГОВОРИМ ДВИЖКУ: "У нас есть предметы, даже если их нет в конфигурации"
        this.hasItems = true;
        this.acceptsItems = true; 
        this.update = true;
        this.solid = true;
        this.hasItems = true;
        this.sync = true;
        this.destructible = true; // Чтобы можно было сломать
    }
});

universalCrafter.buildType = () => extend(Building, {
    progress: 0,
    currentRecipe: null,

    // Определяем, какие предметы здание может принять
    acceptItem(item, source) {
        return this.block.recipes.some(r => r.inputItem === item) && 
               this.items.get(item) < this.block.itemCapacity;
    },

    updateTile() {
        // Если рецепт уже выбран
        if (this.currentRecipe != null) {
            // Учитываем наличие энергии (если блок её потребляет) и скорость (delta)
            this.progress += this.edelta() / this.currentRecipe.craftTime;

            if (this.progress >= 1) {
                this.items.add(this.currentRecipe.outputItem, 1);
                this.progress = 0;
                this.currentRecipe = null;
            }
        } else {
            // Ищем подходящий рецепт по ресурсам внутри
            for (let i = 0; i < this.block.recipes.length; i++) {
                let r = this.block.recipes[i];
                if (this.items.get(r.inputItem) >= r.inputAmount) {
                    this.items.remove(r.inputItem, r.inputAmount);
                    this.currentRecipe = r;
                    break;
                }
            }
        }

        // Автоматическая выгрузка готовых предметов в соседние блоки
        if (this.items.any()) {
            this.dump();
        }
    },

    // Отрисовка полоски прогресса (опционально)
    drawSelect() {
        if (this.currentRecipe != null) {
            Draw.draw(Layer.overlayUI, () => {
                let x = this.x, y = this.y - 8;
                Lines.stroke(2, Color.black);
                Lines.line(x - 4, y, x + 4, y);
                Lines.stroke(1, Color.green);
                Lines.line(x - 4, y, x - 4 + (8 * this.progress), y);
            });
        }
    }
});
