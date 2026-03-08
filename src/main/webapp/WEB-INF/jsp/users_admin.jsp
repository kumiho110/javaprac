<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/site_nav.jspf" %>

<html>
<head>
  <title>Пользователи</title>
</head>
<body>


<h2>Пользователи</h2>

<c:if test="${not empty successMessage}">
  <div style="color: green; margin-bottom: 10px;">${successMessage}</div>
</c:if>

<c:if test="${not empty errorMessage}">
  <div style="color: red; margin-bottom: 10px;">${errorMessage}</div>
</c:if>

<p><a href="/admin/users/new">Создать пользователя</a></p>

<table border="1" cellpadding="6" cellspacing="0">
  <tr>
    <th>ID</th>
    <th>Email</th>
    <th>ФИО</th>
    <th>Телефон</th>
    <th>Роль</th>
    <th>Enabled</th>
    <th>Действия</th>
  </tr>

  <c:forEach var="u" items="${users}">
    <tr>
      <td>${u.id}</td>
      <td>${u.email}</td>
      <td>${u.fullName}</td>
      <td>${u.phone}</td>
      <td>${u.role}</td>
      <td>${u.enabled}</td>
      <td>
        <a href="/admin/users/${u.id}/edit">Редактировать</a>

        <form method="post" action="/admin/users/${u.id}/toggle-enabled" style="display:inline; margin-left: 8px;">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          <button type="submit">
            <c:choose>
              <c:when test="${u.enabled}">Отключить</c:when>
              <c:otherwise>Включить</c:otherwise>
            </c:choose>
          </button>
        </form>

          <form method="post" action="/admin/users/${u.id}/delete" style="display:inline; margin-left: 8px;">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <button type="submit" onclick="return confirm('Удалить пользователя? Все его заказы и резервы будут удалены.')">
                Удалить
            </button>
        </form>
      </td>
    </tr>
  </c:forEach>
</table>

</body>
</html>