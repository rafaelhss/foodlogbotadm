(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('UserTelegramController', UserTelegramController);

    UserTelegramController.$inject = ['UserTelegram'];

    function UserTelegramController(UserTelegram) {

        var vm = this;

        vm.userTelegrams = [];

        loadAll();

        function loadAll() {
            UserTelegram.query(function(result) {
                vm.userTelegrams = result;
                vm.searchQuery = null;
            });
        }
    }
})();
