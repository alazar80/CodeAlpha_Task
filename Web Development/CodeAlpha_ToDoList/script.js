let taskList = document.getElementById("taskList");
let taskInput = document.getElementById("taskInput");

loadTasks();

function addTask() {
  const taskText = taskInput.value.trim();
  if (taskText === "") return;

  const li = createTaskElement(taskText);
  taskList.appendChild(li);
  saveTasks();
  taskInput.value = "";
}

function createTaskElement(text) {
  const li = document.createElement("li");

  const span = document.createElement("span");
  span.textContent = text;
  span.onclick = () => {
    span.classList.toggle("completed");
    saveTasks();
  };

  const deleteBtn = document.createElement("button");
  deleteBtn.textContent = "X";
  deleteBtn.onclick = () => {
    li.remove();
    saveTasks();
  };

  li.appendChild(span);
  li.appendChild(deleteBtn);

  return li;
}

function saveTasks() {
  const tasks = [];
  document.querySelectorAll("#taskList li").forEach(li => {
    tasks.push({
      text: li.querySelector("span").textContent,
      completed: li.querySelector("span").classList.contains("completed")
    });
  });
  localStorage.setItem("tasks", JSON.stringify(tasks));
}

function loadTasks() {
  const saved = localStorage.getItem("tasks");
  if (!saved) return;

  JSON.parse(saved).forEach(task => {
    const li = createTaskElement(task.text);
    if (task.completed) {
      li.querySelector("span").classList.add("completed");
    }
    taskList.appendChild(li);
  });
}
