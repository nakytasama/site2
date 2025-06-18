<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Мой профиль</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/header.css">
    <style>
        html, body {
            margin: 0;
            padding: 0;
            height: 100%;
            width: 100%;
            overflow: hidden;
        }

        .content-container {
            height: calc(100vh - 70px);
            overflow-y: auto;
            overflow-x: hidden;
            padding: 20px;
            box-sizing: border-box;
        }

        .content-container::-webkit-scrollbar {
            display: none;
        }
        .content-container {
            -ms-overflow-style: none;
            scrollbar-width: none;
        }

        .gallery {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 15px;
            max-width: 1200px;
            margin: 0 auto;
        }

        .image-card {
            border: 1px solid #ddd;
            border-radius: 5px;
            overflow: hidden;
            position: relative;
        }

        .image-content {
            cursor: pointer;
        }

        .image-card img {
            width: 100%;
            height: 150px;
            object-fit: cover;
        }

        .image-actions {
            padding: 10px;
            display: flex;
            justify-content: space-around;
        }

        .add-btn {
            margin: 20px;
            padding: 10px 20px;
            background: #4CAF50;
            color: white;
            border: none;
            cursor: pointer;
            display: block;
            margin-left: auto;
            margin-right: auto;
            max-width: 1200px;
        }

        /* стили для модального окна (потом в отдельный файл) */
        .modal {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.9);
            z-index: 1000;
        }
        .modal-container {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .modal-content-wrapper {
            position: relative;
            max-width: 90%;
            max-height: 90vh;
            display: flex;
            flex-direction: column;
            align-items: center;
        }
        .modal-content {
            max-width: 100%;
            max-height: 70vh;
            display: block;
        }
        .modal-nav {
            position: fixed;
            top: 50%;
            transform: translateY(-50%);
            font-size: 40px;
            color: white;
            cursor: pointer;
            background: rgba(0,0,0,0.5);
            width: 50px;
            height: 50px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 50%;
            user-select: none;
            z-index: 1001;
        }
        .modal-nav:hover {
            background: rgba(0,0,0,0.8);
        }
        .prev { left: 20px; }
        .next { right: 20px; }
        .close {
            position: fixed;
            top: 20px;
            right: 20px;
            color: white;
            font-size: 30px;
            cursor: pointer;
            background: rgba(0,0,0,0.5);
            width: 40px;
            height: 40px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 50%;
            line-height: 1;
            z-index: 1001;
        }
        .close:hover {
            background: rgba(0,0,0,0.8);
        }
        .image-description {
            text-align: center;
            color: white;
            padding: 12px 20px;
            background: rgba(0, 0, 0, 0.7);
            border-radius: 8px;
            font-size: 16px;
            margin-top: 15px;
            max-width: 80%;
            width: auto;
            word-break: break-word;
            line-height: 1.4;
        }
    </style>
</head>
<body>
    <%@ include file="includes/header.jsp" %>
    <div class="content-container">
        <button class="add-btn" onclick="location.href='add-image.jsp'">Добавить изображение</button>

        <div class="gallery">
            <c:forEach items="${images}" var="image">
                <div class="image-card">
                    <div class="image-content"
                         data-id="${image.id}"
                         data-title="${image.title}"
                         data-description="${image.description}"
                         data-path="${image.imagePath}">
                        <img src="${pageContext.request.contextPath}/uploads/${image.imagePath}" alt="${image.title}">
                        <div>${image.title}</div>
                    </div>
                    <div class="image-actions">
                        <button onclick="editImage(${image.id})">Редактировать</button>
                        <button onclick="deleteImage(${image.id})">Удалить</button>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

    <!-- модальное окно -->
    <div class="modal" id="imageModal">
        <div class="modal-container">
            <div class="modal-content-wrapper">
                <img class="modal-content" id="modalImage">
                <div class="image-description" id="modalDescription"></div>
            </div>
        </div>
        <span class="close">&times;</span>
        <span class="modal-nav prev">&lt;</span>
        <span class="modal-nav next">&gt;</span>
    </div>

    <script>
        let currentIndex = 0;
        let images = [];
        let modalOpen = false;

        document.querySelectorAll('.image-content').forEach(element => {
            images.push({
                id: parseInt(element.dataset.id),
                title: element.dataset.title,
                description: element.dataset.description,
                imagePath: element.dataset.path
            });
        });

        // нажатие по изображениям
        document.querySelectorAll('.image-content').forEach((element, index) => {
            element.addEventListener('click', function(e) {
                if (!e.target.closest('button')) {
                    currentIndex = index;
                    showImage(images[currentIndex]);
                    document.getElementById('imageModal').style.display = 'block';
                    modalOpen = true;
                }
            });
        });

        // навигация
        function showPrevImage() {
            if (!modalOpen || images.length === 0) return;
            currentIndex = (currentIndex - 1 + images.length) % images.length;
            showImage(images[currentIndex]);
        }

        function showNextImage() {
            if (!modalOpen || images.length === 0) return;
            currentIndex = (currentIndex + 1) % images.length;
            showImage(images[currentIndex]);
        }

        function showImage(image) {
            const modalImage = document.getElementById('modalImage');
            const modalDescription = document.getElementById('modalDescription');

            modalImage.src = '${pageContext.request.contextPath}/uploads/' + image.imagePath;
            modalDescription.textContent = image.description || image.title;
        }

        // закрытие окна
        function closeModal() {
            document.getElementById('imageModal').style.display = 'none';
            modalOpen = false;
        }

        document.querySelector('.prev').addEventListener('click', showPrevImage);
        document.querySelector('.next').addEventListener('click', showNextImage);
        document.querySelector('.close').addEventListener('click', closeModal);

        // чтобы клавиши работали в модальном окне
        document.addEventListener('keydown', (event) => {
            if (!modalOpen) return;

            switch(event.key) {
                case 'Escape':
                    closeModal();
                    break;
                case 'ArrowLeft':
                    showPrevImage();
                    break;
                case 'ArrowRight':
                    showNextImage();
                    break;
            }
        });

        // работа кнопок редактирования и удаления
        function editImage(id) {
            location.href = '${pageContext.request.contextPath}/edit-image?id=' + id;
        }

        function deleteImage(id) {
            if (confirm('Вы уверены, что хотите удалить это изображение?')) {
                fetch('${pageContext.request.contextPath}/image?action=delete&id=' + id, {
                    method: 'POST'
                }).then(response => {
                    if (response.ok) {
                        location.reload();
                    }
                });
            }
        }
    </script>
</body>
</html>