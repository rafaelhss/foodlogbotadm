(function() {
    'use strict';
    angular
        .module('foodlogbotadmApp')
        .factory('Jaca', Jaca);

    Jaca.$inject = ['$resource', 'DateUtils'];

    function Jaca ($resource, DateUtils) {
        var resourceUrl =  'api/jacas/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.jacaDateTime = DateUtils.convertDateTimeFromServer(data.jacaDateTime);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
