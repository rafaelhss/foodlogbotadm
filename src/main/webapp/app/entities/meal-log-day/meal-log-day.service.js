(function() {
    'use strict';
    angular
        .module('foodlogbotadmApp')
        .factory('MealLogDay', MealLogDay);

    MealLogDay.$inject = ['$resource', 'DateUtils'];

    function MealLogDay ($resource, DateUtils) {
        var resourceUrl =  'api/meal-log-days/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.mealLogDayDate = DateUtils.convertDateTimeFromServer(data.mealLogDayDate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
