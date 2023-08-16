package com.revature.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.revature.model.Pet;
import com.revature.util.ConnectionUtility;

public class PetDAOmySQLImpl implements PetDAO {

    @Override
    public Pet getPetById(int id) {
        // 1. connect to db: either connection utility class would work
        try (Connection connection = ConnectionUtility.getConnection()) {
            // 2. create statement
            Statement statement = connection.createStatement();

            // 3. execute statement
            ResultSet rs = statement.executeQuery("SELECT * FROM pets WHERE id = " + id);

            // 4. process results
            while (rs.next()) {
                // get the values from the record and save them somewhere
                int idReturned = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String species = rs.getString("species");
                int vetId = rs.getInt("vet_id");

                return new Pet(idReturned, name, age, species, vetId);
            }

            // 5. close connection
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Pet> getAllPets() {
        // create variables needed
        List<Pet> pets = new ArrayList<>();
        try (Connection connection = ConnectionUtility.getConnection()) {
            Statement statement = connection.createStatement();

            // execute query
            ResultSet rs = statement.executeQuery("SELECT * FROM pets");

            // process results
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String species = rs.getString("species");
                int vetId = rs.getInt("vet_id");

                pets.add(new Pet(id, name, age, species, vetId));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return pets;
    }

    @Override
    public Pet addPet(Pet pet) {
        try (Connection connection = ConnectionUtility.getConnection()) {
            // sql
            String sql = "INSERT INTO pets(name, age, species, vet_id) VALUES (?,?,?,?)";

            // create statement:
            // https://docs.oracle.com/javase%2F7%2Fdocs%2Fapi%2F%2F/java/sql/Statement.html
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, pet.getName());
            statement.setInt(2, pet.getAge());
            statement.setString(3, pet.getSpecies());
            statement.setInt(4, pet.getVetId());

            // execute statement
            statement.executeUpdate();

            // process
            // get the keys
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                return new Pet(keys.getInt(1), pet.getName(), pet.getAge(), pet.getSpecies(), pet.getVetId());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean updatePet(Pet pet) {

        try (Connection connection = ConnectionUtility.getConnection()) {

            String sql = "UPDATE pets SET name = ?, age = ?, species = ?, vet_id = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, pet.getName());
            ps.setInt(2, pet.getAge());
            ps.setString(3, pet.getSpecies());
            ps.setInt(4, pet.getVetId());
            ps.setInt(5, pet.getId());

            int numberOfUpdatedRows = ps.executeUpdate();

            if (numberOfUpdatedRows != 0) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deletePetById(int id) {
        try (Connection connection = ConnectionUtility.getConnection()) {

            String sql = "DELETE FROM pets WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);

            int numberOfUpdatedRows = ps.executeUpdate();

            if (numberOfUpdatedRows != 0) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

}
