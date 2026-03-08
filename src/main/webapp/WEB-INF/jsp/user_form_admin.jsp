<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/site_nav.jspf" %>

<html>
<head>
  <title>Редактирование пользователя</title>
</head>
<body>

<c:if test="${not empty successMessage}">
  <div style="color: green; margin-bottom: 10px;">${successMessage}</div>
</c:if>

<c:if test="${not empty errorMessage}">
  <div style="color: red; margin-bottom: 10px;">${errorMessage}</div>
</c:if>

<h2>Редактирование пользователя</h2>

<form method="post" action="/admin/users/${user.id}">
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

  <div style="margin-bottom: 8px;">
    <label>ФИО</label><br/>
    <input type="text" name="fullName" value="${user.fullName}" required style="width: 420px;"/>
  </div>

  <div style="margin-bottom: 8px;">
    <label>Телефон</label><br/>
    <input type="text" name="phone" value="${user.phone}" style="width: 320px;"/>
  </div>

  <div style="margin-bottom: 8px;">
    <label>Адрес</label><br/>
    <textarea name="address" rows="4" cols="60">${user.address}</textarea>
  </div>

<div style="margin-bottom: 8px;">
  <label>Роль</label><br/>
  <strong>${user.role}</strong>
</div>

  <div style="margin-bottom: 8px;">
    <label>
      <input type="checkbox" name="enabled" value="true" <c:if test="${user.enabled}">checked</c:if> />
      Аккаунт включён
    </label>
  </div>

  <button type="submit">Сохранить</button>
</form>

<hr/>


<h2>Учётные данные</h2>

<form method="post" action="/admin/users/${user.id}/credentials" autocomplete="off">
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

  <div style="margin-bottom: 8px;">
    <label>Email</label><br/>
    <input
            type="email"
            name="email"
            value="${user.email}"
            required
            autocomplete="off"
            style="width: 320px;"
    />
  </div>

  <div style="margin-bottom: 8px;">
    <label>Новый пароль</label><br/>
    <input
            type="password"
            name="resetValue1"
            minlength="6"
            autocomplete="off"
            style="width: 320px;"
    />
  </div>

  <div style="margin-bottom: 8px;">
    <label>Подтверждение нового пароля</label><br/>
    <input
            type="password"
            name="resetValue2"
            minlength="6"
            autocomplete="off"
            style="width: 320px;"
    />
  </div>

  <button type="submit">Сохранить учётные данные</button>
</form>

</body>
</html>