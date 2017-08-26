(function() {
    'use strict';
    angular
        .module('foodlogbotadmApp')
        .factory('UserTelegram', UserTelegram);

    UserTelegram.$inject = ['$resource'];

    function UserTelegram ($resource) {
        var resourceUrl =  'api/user-telegrams/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
