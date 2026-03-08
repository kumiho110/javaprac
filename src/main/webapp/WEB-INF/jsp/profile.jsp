<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/site_nav.jspf" %>

<html>
<head>
  <title>Профиль</title>
</head>
<body>


<h2>Профиль</h2>

<c:if test="${not empty success}">
  <p style="color: green;"><b>${success}</b></p>
</c:if>

<c:if test="${not empty error}">
  <p style="color: red;"><b>${error}</b></p>
</c:if>

<form method="post" action="/me">
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

  <div style="margin-bottom: 8px;">
    <label>Email:</label><br/>
    <input type="email" value="${user.email}" readonly style="width: 320px;"/>
  </div>

  <div style="margin-bottom: 8px;">
    <label>ФИО:</label><br/>
    <input type="text" name="fullName" value="${user.fullName}" required style="width: 420px;"/>
  </div>

  <div style="margin-bottom: 8px;">
    <label>Телефон:</label><br/>
    <input type="text" name="phone" value="${user.phone}" style="width: 320px;"/>
  </div>

  <div style="margin-bottom: 8px;">
    <label>Адрес:</label><br/>
    <textarea name="address" rows="4" cols="60">${user.address}</textarea>
  </div>

  <button type="submit">Сохранить</button>
</form>

<hr/>

<h2>Смена пароля</h2>

<form method="post" action="/me/password">
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

  <div style="margin-bottom: 8px;">
    <label>Email</label><br/>
    <input type="email" value="${user.email}" readonly autocomplete="username" style="width: 320px;"/>
  </div>

  <div style="margin-bottom: 8px;">
    <label>Текущий пароль</label><br/>
    <input type="password" name="currentPassword" required autocomplete="current-password" style="width: 320px;"/>
  </div>

  <div style="margin-bottom: 8px;">
    <label>Новый пароль</label><br/>
    <input type="password" name="newPassword" required minlength="6" autocomplete="new-password" style="width: 320px;"/>
  </div>

  <div style="margin-bottom: 8px;">
    <label>Подтверждение нового пароля</label><br/>
    <input type="password" name="newPassword2" required minlength="6" autocomplete="new-password" style="width: 320px;"/>
  </div>

  <button type="submit">Изменить пароль</button>
</form>

</body>
</html>