<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/site_nav.jspf" %>

<html>
<head>
  <title>Создание пользователя</title>
</head>
<body>

<c:if test="${not empty successMessage}">
  <div style="color: green; margin-bottom: 10px;">${successMessage}</div>
</c:if>

<c:if test="${not empty errorMessage}">
  <div style="color: red; margin-bottom: 10px;">${errorMessage}</div>
</c:if>

<h2>Создание пользователя</h2>

<form method="post" action="/admin/users" autocomplete="off">
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

  <div style="margin-bottom: 8px;">
    <label>Email</label><br/>
    <input type="email" name="email" required style="width: 320px;" autocomplete="off"/>
  </div>

  <div style="margin-bottom: 8px;">
    <label>Пароль</label><br/>
    <input type="password" name="password" required minlength="6" style="width: 320px;" autocomplete="off"/>
  </div>

  <div style="margin-bottom: 8px;">
    <label>Подтверждение пароля</label><br/>
    <input type="password" name="password2" required minlength="6" style="width: 320px;" autocomplete="off"/>
  </div>

  <div style="margin-bottom: 8px;">
    <label>ФИО</label><br/>
    <input type="text" name="fullName" required style="width: 420px;"/>
  </div>

  <div style="margin-bottom: 8px;">
    <label>Телефон</label><br/>
    <input type="text" name="phone" style="width: 320px;"/>
  </div>

  <div style="margin-bottom: 8px;">
    <label>Адрес</label><br/>
    <textarea name="address" rows="4" cols="60"></textarea>
  </div>

  <div style="margin-bottom: 8px;">
    <label>Роль</label><br/>
    <select name="role">
      <c:forEach var="r" items="${roles}">
        <option value="${r}">${r}</option>
      </c:forEach>
    </select>
  </div>

  <div style="margin-bottom: 8px;">
    <label>
      <input type="checkbox" name="enabled" value="true" checked />
      Аккаунт включён
    </label>
  </div>

  <button type="submit">Создать</button>
</form>

<p style="margin-top: 16px;">
  <a href="/admin/users">Назад к списку пользователей</a>
</p>

</body>
</html>