<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="adminlte2" />
    <g:set var="entityName" value="${message(code: 'langCode.label', default: 'LangCode')}" />
    <title><g:message code="default.list.label" args="[entityName]" /></title>
</head>
<body>
<div class="row">
    <section class="content-header">
        <div class="pull-right">
            <ul>
                <g:link class="btn btn-success" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link>
            </ul>
        </div>
    </section>
</div>
<div class="row">
    <div class="col-md-12">
        <div class="box box-primary">
            <div class="box-header with-border">
                <h3 class="box-title"><g:message code="default.list.label" args="[entityName]" /></h3>
            </div>
            <div class="box-body">
                <g:if test="${flash.message}">
                    <div class="alert alert-success" role="status">${flash.message}</div>
                </g:if>
                <table id="datatable-entity" class="table table-striped table-bordered">
                    <thead>
                        <tr>
                            <td>id</td>
                            <td>localeCode</td>
                            <td>langCode</td>
                        </tr>
                    </thead>
                    <tbody>
                        <g:each in="${langCodeList}" var="obj" status="i">
                            <tr>
                                <td>
                                    <g:link resource="langCode" id="${obj.id}" action="show">${obj.id}</g:link>
                                </td>
                                <td>${obj?.localeCode}</td>
                                <td>${obj?.langCode}</td>
                            </tr>
                        </g:each>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<g:render template="/layouts/dataTables"/>
</body>
</html>